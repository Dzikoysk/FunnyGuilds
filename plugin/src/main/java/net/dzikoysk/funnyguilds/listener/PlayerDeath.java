package net.dzikoysk.funnyguilds.listener;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import net.dzikoysk.funnyguilds.damage.Damage;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.CombatPointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.CombatPointsChangeEvent.CombatTable.Assist;
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerDeath extends AbstractFunnyListener {

    private final RankSystem rankSystem;

    public PlayerDeath(PluginConfiguration config) {
        this.rankSystem = RankSystem.create(config);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player playerVictim = event.getEntity();
        Player playerAttacker = event.getEntity().getKiller();

        Option<User> victimOption = this.userManager.findByPlayer(playerVictim);
        if (victimOption.isEmpty()) {
            return;
        }

        User victim = victimOption.get();
        DamageState victimDamageState = damageManager.getDamageState(victim.getUUID());

        Option<User> attackerOption = Option.none();
        if (playerAttacker == null && this.config.considerLastAttackerAsKiller || playerVictim.equals(playerAttacker)) { // If player killed himself use last attacker as a killer
            Option<Damage> lastDamageOption = victimDamageState.getLastDamage();
            if (lastDamageOption.isEmpty() || !lastDamageOption.get().getAttacker().isOnline()) {
                this.handleDeathEvent(victim, victim, EventCause.USER);
                victimDamageState.clear();
                return;
            }
            Damage lastDamage = lastDamageOption.get();

            if (lastDamage.isExpired(this.config.lastAttackerAsKillerConsiderationTimeout)) {
                this.handleDeathEvent(victim, victim, EventCause.USER);
                victimDamageState.clear();
                return;
            }

            attackerOption = Option.of(lastDamage.getAttacker());
            playerAttacker = this.funnyServer.getPlayer(lastDamage.getAttacker().getUUID()).get();
        }

        if (attackerOption.isEmpty() && playerAttacker != null && !playerVictim.equals(playerAttacker)) { // If player killed himself do not use him as a killer
            attackerOption = this.userManager.findByPlayer(playerAttacker);
        }

        this.handleDeathEvent(victim, attackerOption.orElseGet(victim), attackerOption.isPresent() ? EventCause.COMBAT : EventCause.USER);

        if (attackerOption.isEmpty()) {
            return;
        }
        User attacker = attackerOption.get();
        DamageState attackerDamageState = this.damageManager.getDamageState(attacker.getUUID());

        if (victim.equals(attacker)) {
            victimDamageState.clear();
            return;
        }

        if (HookManager.WORLD_GUARD.isPresent()) {
            WorldGuardHook worldGuard = HookManager.WORLD_GUARD.get();
            if (worldGuard.isInNonPointsRegion(playerVictim.getLocation()) || worldGuard.isInNonPointsRegion(playerAttacker.getLocation())) {
                victimDamageState.clear();
                return;
            }
        }

        if (this.checkRankFarmingProtection(playerVictim, playerAttacker, victim, victimDamageState, attacker, attackerDamageState)) {
            victimDamageState.clear();
            event.setDeathMessage(null);
            return;
        }

        if (this.checkIPRankFarmingProtection(playerVictim, playerAttacker)) {
            victimDamageState.clear();
            event.setDeathMessage(null);
            return;
        }

        if (this.checkMemberRankChangeProtection(victim, attacker)) {
            victimDamageState.clear();
            event.setDeathMessage(null);
            return;
        }

        if (this.checkAllyRankChangeProtection(victim, attacker)) {
            attackerDamageState.clear();
            event.setDeathMessage(null);
            return;
        }

        KillsChangeEvent killsChangeEvent = new KillsChangeEvent(EventCause.COMBAT, attacker, victim, 1);
        if (SimpleEventHandler.handle(killsChangeEvent)) {
            attacker.getRank().updateKills(currentValue -> currentValue + killsChangeEvent.getKillsChange());
        }

        victimDamageState.addKill(attacker);

        int victimPoints = victim.getRank().getPoints();
        int attackerPoints = attacker.getRank().getPoints();

        RankSystem.RankResult result = this.rankSystem.calculate(this.config.rankSystem, attackerPoints, victimPoints);

        List<User> messageReceivers = new ArrayList<>();

        messageReceivers.add(attacker);
        messageReceivers.add(victim);

        Map<User, Assist> calculatedAssists = this.handleAssists(victim, victimDamageState, attacker, result, messageReceivers);

        int addedAttackerPoints = (!this.config.assistKillerAlwaysShare && calculatedAssists.isEmpty())
                ? result.getAttackerPoints()
                : (int) Math.round(result.getAttackerPoints() * this.config.assistKillerShare);

        PointsChangeEvent attackerPointsChangeEvent = new PointsChangeEvent(EventCause.COMBAT, victim, attacker, addedAttackerPoints);
        if (!SimpleEventHandler.handle(attackerPointsChangeEvent)) {
            attackerPointsChangeEvent.setPointsChange(0);
        }

        PointsChangeEvent victimPointsChangeEvent = new PointsChangeEvent(EventCause.COMBAT, attacker, victim, -result.getVictimPoints());
        if (!SimpleEventHandler.handle(victimPointsChangeEvent)) {
            victimPointsChangeEvent.setPointsChange(0);
        }

        CombatPointsChangeEvent combatPointsChangeEvent = new CombatPointsChangeEvent(
                EventCause.COMBAT,
                attacker,
                victim,
                attackerPointsChangeEvent.getPointsChange(),
                victimPointsChangeEvent.getPointsChange(),
                calculatedAssists
        );

        if (SimpleEventHandler.handle(combatPointsChangeEvent)) {
            attacker.getRank().updatePoints(currentValue -> currentValue + combatPointsChangeEvent.getAttackerPointsChange());
            victim.getRank().updatePoints(currentValue -> currentValue + combatPointsChangeEvent.getVictimPointsChange());

            combatPointsChangeEvent.getAssistsMap()
                    .getPointChanges()
                    .forEach((user, points) -> user.getRank().updatePoints(currentValue -> currentValue + points));
        }

        victimDamageState.clear();

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();
        if (this.config.dataModel == DataModel.MYSQL) {
            victim.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));
            attacker.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));
            PandaStream.of(calculatedAssists.keySet())
                    .flatMap(User::getGuild)
                    .forEach(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
            calculatedAssists.keySet().forEach(assistUser -> taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(assistUser)));
        }

        ConcurrencyTaskBuilder updateUserRequests = taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(this.plugin, victim))
                .delegate(new DummyGlobalUpdateUserRequest(this.plugin, attacker));
        PandaStream.of(calculatedAssists.keySet())
                .map(user -> new DummyGlobalUpdateUserRequest(this.plugin, user))
                .forEach(taskBuilder::delegate);

        this.concurrencyManager.postTask(updateUserRequests.build());

        int attackerPointsChange = combatPointsChangeEvent.getAttackerPointsChange();
        int victimPointsChange = Math.min(victimPoints, combatPointsChangeEvent.getVictimPointsChange());

        List<String> formattedAssists = this.formatAssists(combatPointsChangeEvent.getAssistsMap().getAssistsMap());

        FunnyFormatter killFormatter = new FunnyFormatter()
                .register("{ATTACKER}", attacker.getName())
                .register("{VICTIM}", victim.getName())
                .register("{+}", attackerPointsChange)
                .register("{-}", victimPointsChange)
                .register("{PLUS-FORMATTED}", NumberRange.inRangeToString(attackerPointsChange, this.config.killPointsChangeFormat, true))
                .register("{CHANGE}", Math.abs(attackerPointsChange))
                .register("{MINUS-FORMATTED}", NumberRange.inRangeToString(victimPointsChange, this.config.killPointsChangeFormat, true))
                .register("{CHANGE}", Math.abs(victimPointsChange))
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(victimPoints, this.config.pointsFormat, true))
                .register("{POINTS}", victim.getRank().getPoints())
                .register("{WEAPON}", MaterialUtils.getMaterialName(playerAttacker.getItemInHand().getType()))
                .register("{WEAPON-NAME}", MaterialUtils.getItemCustomName(playerAttacker.getItemInHand()))
                .register("{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", playerAttacker.getHealth()))
                .register("{REMAINING-HEARTS}", (int) (playerAttacker.getHealth() / 2))
                .register("{VTAG}", victim.getGuild()
                        .map(guild -> FunnyFormatter.format(this.config.chatGuild.getValue(), "{TAG}", guild.getTag()))
                        .orElseGet(""))
                .register("{ATAG}", attacker.getGuild()
                        .map(guild -> FunnyFormatter.format(this.config.chatGuild.getValue(), "{TAG}", guild.getTag()))
                        .orElseGet(""))
                .register("{ASSISTS}", !formattedAssists.isEmpty()
                        ? FunnyFormatter.format(this.messages.rankAssistMessage, "{ASSISTS}", FunnyStringUtils.join(formattedAssists, this.messages.rankAssistDelimiter))
                        : "");

        if (this.config.displayTitleNotificationForKiller) {
            TitleMessage titleMessage = TitleMessage.builder()
                    .text(killFormatter.format(this.messages.rankKillTitle))
                    .subText(killFormatter.format(this.messages.rankKillSubtitle))
                    .fadeInDuration(this.config.notificationTitleFadeIn)
                    .stayDuration(this.config.notificationTitleStay)
                    .fadeOutDuration(this.config.notificationTitleFadeOut)
                    .build();

            this.messageAccessor.sendTitleMessage(titleMessage, playerAttacker);
        }

        String deathMessage = killFormatter.format(this.messages.rankDeathMessage);

        if (this.config.broadcastDeathMessage) {
            if (this.config.ignoreDisabledDeathMessages) {
                event.getEntity().getWorld().getPlayers().forEach(player -> {
                    event.setDeathMessage(null);
                    ChatUtils.sendMessage(player, deathMessage);
                });
            }
            else {
                event.setDeathMessage(deathMessage);
            }
        }
        else {
            event.setDeathMessage(null);
            messageReceivers.forEach(fighter -> fighter.sendMessage(deathMessage));
        }
    }

    private void handleDeathEvent(User victim, User attacker, EventCause cause) {
        DeathsChangeEvent deathsChangeEvent = new DeathsChangeEvent(cause, attacker, victim, 1);
        if (SimpleEventHandler.handle(deathsChangeEvent)) {
            victim.getRank().updateDeaths(currentValue -> currentValue + deathsChangeEvent.getDeathsChange());
        }
    }

    // Function to check if player is rank farming (killing player indefinitely to get points)
    private boolean checkRankFarmingProtection(Player playerVictim, Player playerAttacker, User victim, DamageState victimDamageState, User attacker, DamageState attackerDamageState) {
        if (!this.config.rankFarmingProtect) {
            return false;
        }

        Option<Instant> victimTimestamp = victimDamageState.getLastKillTime(attacker);
        Option<Instant> attackerTimestamp = attackerDamageState.getLastKillTime(victim);

        if (victimTimestamp.is(timestamp -> Duration.between(timestamp, Instant.now()).compareTo(this.config.rankFarmingCooldown) < 0)) {
            ChatUtils.sendMessage(playerVictim, this.messages.rankLastVictimV);
            ChatUtils.sendMessage(playerAttacker, this.messages.rankLastVictimA);

            return true;
        }
        else if (this.config.bidirectionalRankFarmingProtect && attackerTimestamp.is(timestamp -> Duration.between(timestamp, Instant.now()).compareTo(this.config.rankFarmingCooldown) < 0)) {
            ChatUtils.sendMessage(playerVictim, this.messages.rankLastAttackerV);
            ChatUtils.sendMessage(playerAttacker, this.messages.rankLastAttackerA);

            return true;
        }

        return false;
    }

    // Function to check if player is rank farming (killing player indefinitely to get points)
    private boolean checkIPRankFarmingProtection(Player playerVictim, Player playerAttacker) {
        if (!this.config.rankIPProtect) {
            return false;
        }

        String attackerIP = playerAttacker.getAddress().getHostString();
        if (attackerIP != null && attackerIP.equalsIgnoreCase(playerVictim.getAddress().getHostString())) {
            ChatUtils.sendMessage(playerVictim, this.messages.rankIPVictim);
            ChatUtils.sendMessage(playerAttacker, this.messages.rankIPAttacker);
            return true;
        }

        return false;
    }

    // Function to check if both players are in the same guild
    private boolean checkMemberRankChangeProtection(User victim, User attacker) {
        if (!this.config.rankMemberProtect) {
            return false;
        }

        if (!victim.hasGuild() || !attacker.hasGuild()) {
            return false;
        }

        if (victim.getGuild().equals(attacker.getGuild())) {
            victim.sendMessage(this.messages.rankMemberVictim);
            attacker.sendMessage(this.messages.rankMemberAttacker);
            return true;
        }

        return false;
    }

    // Function to check if both players are in the allied guild
    private boolean checkAllyRankChangeProtection(User victim, User attacker) {
        if (!this.config.rankAllyProtect) {
            return false;
        }

        Option<Guild> victimGuildOption = victim.getGuild();
        Option<Guild> attackerGuildOption = attacker.getGuild();
        if (victimGuildOption.isEmpty() || attackerGuildOption.isEmpty()) {
            return false;
        }
        Guild victimGuild = victimGuildOption.get();
        Guild attackerGuild = attackerGuildOption.get();

        if (victimGuild.isAlly(attackerGuild) || attackerGuild.isAlly(victimGuild)) {
            victim.sendMessage(this.messages.rankAllyVictim);
            attacker.sendMessage(this.messages.rankAllyAttacker);
            return true;
        }

        return false;
    }

    // This method calculate how many points assisting players should receive
    // Returns a map of players that were assisting
    private Map<User, Assist> handleAssists(User victim, DamageState victimDamageState, User attacker, RankSystem.RankResult result, List<User> messageReceivers) {
        Map<User, Assist> calculatedAssists = new HashMap<>();

        if (!this.config.assistEnable) {
            return calculatedAssists;
        }

        Map<User, Double> damageMap = new HashMap<>(victimDamageState.getSortedTotalDamageMap());
        damageMap.remove(attacker);

        double toShare = result.getAttackerPoints() * (1 - this.config.assistKillerShare);
        double totalDamage = victimDamageState.getTotalDamage();

        int assistsCount = 0;
        for (Entry<User, Double> assist : damageMap.entrySet()) {
            User assistUser = assist.getKey();
            double dealtDamage = assist.getValue();

            double assistFraction = dealtDamage / totalDamage;
            int addedPoints = (int) Math.round(assistFraction * toShare);

            if (addedPoints <= 0) {
                continue;
            }

            if (this.config.assistsLimit > 0) {
                if (assistsCount >= this.config.assistsLimit) {
                    break;
                }

                assistsCount++;
            }

            PointsChangeEvent assistPointsChangeEvent = new PointsChangeEvent(EventCause.COMBAT, victim, assistUser, addedPoints);
            if (!SimpleEventHandler.handle(assistPointsChangeEvent)) {
                assistPointsChangeEvent.setPointsChange(0);
            }
            calculatedAssists.put(assistUser, new Assist(assistPointsChangeEvent.getPointsChange(), assistFraction));

            AssistsChangeEvent assistsChangeEvent = new AssistsChangeEvent(EventCause.COMBAT, victim, assistUser, 1);
            if (SimpleEventHandler.handle(assistsChangeEvent)) {
                assistUser.getRank().updateAssists(currentValue -> currentValue + assistsChangeEvent.getAssistsChange());
            }

            messageReceivers.add(assistUser);
        }

        return calculatedAssists;
    }

    private List<String> formatAssists(Map<User, Assist> assists) {
        List<String> formattedAssists = new ArrayList<>();
        assists.forEach((user, assist) -> {
            int points = assist.getPointsChange();
            double damageShare = assist.getDamageShare();

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{PLAYER}", user.getName())
                    .register("{+}", points)
                    .register("{PLUS-FORMATTED}", NumberRange.inRangeToString(points, this.config.killPointsChangeFormat, true))
                    .register("{CHANGE}", Math.abs(points))
                    .register("{SHARE}", FunnyStringUtils.getPercent(damageShare));
            formattedAssists.add(formatter.format(this.messages.rankAssistEntry));
        });
        return formattedAssists;
    }

}
