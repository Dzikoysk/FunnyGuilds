package net.dzikoysk.funnyguilds.listener;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.FunnyMessageDispatcher;
import net.dzikoysk.funnyguilds.damage.Damage;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildPointsAsyncTask;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateUserPointsAsyncTask;
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
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

        Map<User, Assist> calculatedAssists = this.handleAssists(victim, victimDamageState, attacker, result);

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

        if (this.config.dataModel.isSQL()) {
            victim.getGuild().peek(guild -> this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildPointsAsyncTask(guild)));
            attacker.getGuild().peek(guild -> this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildPointsAsyncTask(guild)));

            PandaStream.of(calculatedAssists.keySet())
                    .flatMap(User::getGuild)
                    .forEach(guild -> this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildPointsAsyncTask(guild)));

            this.plugin.scheduleFunnyTasks(
                    new DatabaseUpdateUserPointsAsyncTask(victim),
                    new DatabaseUpdateUserPointsAsyncTask(attacker)
            );

            calculatedAssists.keySet().forEach(assistUser ->
                    this.plugin.scheduleFunnyTasks(new DatabaseUpdateUserPointsAsyncTask(assistUser))
            );
        }

        this.plugin.getDummyManager().peek(manager -> {
            this.plugin.scheduleFunnyTasks(
                    new ScoreboardGlobalUpdateUserSyncTask(manager, victim),
                    new ScoreboardGlobalUpdateUserSyncTask(manager, attacker)
            );
            calculatedAssists.keySet().forEach(user -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, user)));
        });

        int attackerPointsChange = combatPointsChangeEvent.getAttackerPointsChange();
        int victimPointsChange = Math.min(victimPoints, combatPointsChangeEvent.getVictimPointsChange());

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
                        .orElseGet(""));

        Replaceable itemReplacement = ItemComponentHelper.prepareItemReplacement(playerAttacker.getItemInHand());

        if (this.config.displayNotificationForKiller) {
            this.messageService.getMessage(config -> config.rankKillMessage)
                    .with(killFormatter)
                    .with(itemReplacement)
                    .receiver(attacker)
                    .send();
        }

        if (this.config.disableDefaultDeathMessage) {
            event.setDeathMessage(null);
        }

        FunnyMessageDispatcher deathMessage = this.messageService.getMessage(config -> config.rankDeathMessage)
                .with(killFormatter)
                .with(itemReplacement)
                .with(CommandSender.class, receiver -> {
                    String assistsMessage = "";
                    CombatPointsChangeEvent.CombatTable combatTable = combatPointsChangeEvent.getAssistsMap();
                    if (!combatTable.isEmpty()) {
                        List<String> formattedAssists = this.formatAssists(receiver, combatTable.getAssistsMap());
                        String assistsDelimiter = this.messageService.get(receiver, config -> config.rankAssistDelimiter);
                        assistsMessage = this.messageService.get(
                                receiver,
                                config -> config.rankAssistMessage,
                                Replacement.of("{ASSISTS}", FunnyStringUtils.join(formattedAssists, assistsDelimiter))
                        );
                    }
                    return Replacement.of("{ASSISTS}", assistsMessage);
                })
                .receiver(attacker)
                .receiver(victim)
                .receivers(calculatedAssists.keySet())
                .console();

        switch (this.config.deathMessageReceivers) {
            case GUILD:
                attacker.getGuild().peek(guild -> deathMessage.receivers(guild.getOnlineMembers()));
                victim.getGuild().peek(guild -> deathMessage.receivers(guild.getOnlineMembers()));
                calculatedAssists.keySet().forEach(user -> user.getGuild().peek(guild -> deathMessage.receivers(guild.getOnlineMembers())));
                break;
            case WORLD:
                deathMessage.receivers(event.getEntity().getWorld().getPlayers());
                break;
            case ALL:
                deathMessage.receivers(Bukkit.getOnlinePlayers());
                break;
        }

        deathMessage.send();
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
            this.messageService.getMessage(config -> config.rankLastVictimV)
                    .receiver(playerVictim)
                    .send();
            this.messageService.getMessage(config -> config.rankLastVictimA)
                    .receiver(playerAttacker)
                    .send();

            return true;
        }
        else if (this.config.bidirectionalRankFarmingProtect && attackerTimestamp.is(timestamp -> Duration.between(timestamp, Instant.now()).compareTo(this.config.rankFarmingCooldown) < 0)) {
            this.messageService.getMessage(config -> config.rankLastAttackerV)
                    .receiver(playerVictim)
                    .send();
            this.messageService.getMessage(config -> config.rankLastAttackerA)
                    .receiver(playerAttacker)
                    .send();

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
            this.messageService.getMessage(config -> config.rankIPVictim)
                    .receiver(playerVictim)
                    .send();
            this.messageService.getMessage(config -> config.rankIPAttacker)
                    .receiver(playerAttacker)
                    .send();
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
            this.messageService.getMessage(config -> config.rankMemberVictim)
                    .receiver(victim)
                    .send();
            this.messageService.getMessage(config -> config.rankMemberAttacker)
                    .receiver(attacker)
                    .send();
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
            this.messageService.getMessage(config -> config.rankAllyVictim)
                    .receiver(victim)
                    .send();
            return true;
        }

        return false;
    }

    // This method calculate how many points assisting players should receive
    // Returns a map of players that were assisting
    private Map<User, Assist> handleAssists(User victim, DamageState victimDamageState, User attacker, RankSystem.RankResult result) {
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
        }

        return calculatedAssists;
    }

    private List<String> formatAssists(CommandSender receiver, Map<User, Assist> assists) {
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
            formattedAssists.add(this.messageService.get(receiver, config -> config.rankAssistEntry, formatter));
        });
        return formattedAssists;
    }

}
