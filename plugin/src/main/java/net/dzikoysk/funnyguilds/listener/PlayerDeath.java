package net.dzikoysk.funnyguilds.listener;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.DamageCache;
import net.dzikoysk.funnyguilds.user.DamageCache.Damage;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import panda.std.Option;
import panda.std.Pair;
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
        UserCache victimCache = victim.getCache();
        DamageCache victimDamageCache = victimCache.getDamageCache();

        Option<User> attackerOption = Option.none();
        if (playerAttacker == null && this.config.considerLastAttackerAsKiller) {
            Option<Damage> lastDamageOption = victimDamageCache.getLastDamage();
            if (lastDamageOption.isEmpty() || !lastDamageOption.get().getAttacker().isOnline()) {
                this.handleDeathEvent(victim, victim);
                victimDamageCache.clear();
                return;
            }
            Damage lastDamage = lastDamageOption.get();

            if (lastDamage.isExpired(this.config.lastAttackerAsKillerConsiderationTimeout)) {
                this.handleDeathEvent(victim, victim);
                victimDamageCache.clear();
                return;
            }

            attackerOption = Option.of(lastDamage.getAttacker());
            playerAttacker = this.funnyServer.getPlayer(lastDamage.getAttacker().getUUID()).get();
        }

        if (attackerOption.isEmpty() && playerAttacker != null) {
            attackerOption = this.userManager.findByPlayer(playerAttacker);
        }

        this.handleDeathEvent(victim, attackerOption.orElseGet(victim));

        if (attackerOption.isEmpty()) {
            return;
        }
        User attacker = attackerOption.get();
        UserCache attackerCache = attacker.getCache();
        DamageCache attackerDamageCache = attackerCache.getDamageCache();

        if (victim.equals(attacker)) {
            victimDamageCache.clear();
            return;
        }

        if (HookManager.WORLD_GUARD.isPresent()) {
            WorldGuardHook worldGuard = HookManager.WORLD_GUARD.get();
            if (worldGuard.isInNonPointsRegion(playerVictim.getLocation()) || worldGuard.isInNonPointsRegion(playerAttacker.getLocation())) {
                victimDamageCache.clear();
                return;
            }
        }

        if (this.checkRankFarmingProtection(playerVictim, playerAttacker, victim, victimDamageCache, attacker, attackerDamageCache)) {
            victimDamageCache.clear();
            event.setDeathMessage(null);
            return;
        }

        if (this.checkIPRankFarmingProtection(playerVictim, playerAttacker)) {
            victimDamageCache.clear();
            event.setDeathMessage(null);
            return;
        }

        KillsChangeEvent killsChangeEvent = new KillsChangeEvent(EventCause.USER, attacker, victim, 1);
        if (SimpleEventHandler.handle(killsChangeEvent)) {
            attacker.getRank().updateKills(currentValue -> currentValue + killsChangeEvent.getKillsChange());
        }

        victimDamageCache.addKill(attacker);

        int victimPoints = victim.getRank().getPoints();
        int attackerPoints = attacker.getRank().getPoints();

        RankSystem.RankResult result = this.rankSystem.calculate(this.config.rankSystem, attackerPoints, victimPoints);

        List<User> messageReceivers = new ArrayList<>();

        messageReceivers.add(attacker);
        messageReceivers.add(victim);

        Pair<Set<User>, List<String>> assistsResult = this.calculateAssists(victim, victimDamageCache, attacker, result, messageReceivers);
        Set<User> assistUsers = assistsResult.getFirst();
        List<String> assistEntries = assistsResult.getSecond();

        int addedAttackerPoints = (!this.config.assistKillerAlwaysShare && assistUsers.isEmpty())
                ? result.getAttackerPoints()
                : (int) Math.round(result.getAttackerPoints() * this.config.assistKillerShare);

        PointsChangeEvent attackerPointsChangeEvent = new PointsChangeEvent(EventCause.USER, victim, attacker, addedAttackerPoints);
        if (SimpleEventHandler.handle(attackerPointsChangeEvent)) {
            attacker.getRank().updatePoints(currentValue -> currentValue + attackerPointsChangeEvent.getPointsChange());
        }

        PointsChangeEvent victimPointsChangeEvent = new PointsChangeEvent(EventCause.USER, attacker, victim, -result.getVictimPoints());
        if (SimpleEventHandler.handle(victimPointsChangeEvent)) {
            victim.getRank().updatePoints(currentValue -> currentValue + victimPointsChangeEvent.getPointsChange());
        }

        victimDamageCache.clear();

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();
        if (this.config.dataModel == DataModel.MYSQL) {
            victim.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));
            attacker.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));
            PandaStream.of(assistUsers).flatMap(User::getGuild).forEach(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
            PandaStream.of(assistUsers).forEach(assistUser -> taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(assistUser)));
        }

        ConcurrencyTaskBuilder updateUserRequests = taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(victim))
                .delegate(new DummyGlobalUpdateUserRequest(attacker));
        PandaStream.of(assistUsers).map(DummyGlobalUpdateUserRequest::new).forEach(taskBuilder::delegate);

        this.concurrencyManager.postTask(updateUserRequests.build());

        int attackerPointsChange = attackerPointsChangeEvent.getPointsChange();
        int victimPointsChange = Math.min(victimPoints, victimPointsChangeEvent.getPointsChange());

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
                .register("{ASSISTS}", !assistEntries.isEmpty()
                        ? FunnyFormatter.format(this.messages.rankAssistMessage, "{ASSISTS}", String.join(this.messages.rankAssistDelimiter, assistEntries))
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

    private void handleDeathEvent(User victim, User attacker) {
        DeathsChangeEvent deathsChangeEvent = new DeathsChangeEvent(EventCause.USER, attacker, victim, 1);
        if (SimpleEventHandler.handle(deathsChangeEvent)) {
            victim.getRank().updateDeaths(currentValue -> currentValue + deathsChangeEvent.getDeathsChange());
        }
    }

    // Function to check if player is rank farming (killing player indefinitely to get points)
    private boolean checkRankFarmingProtection(Player playerVictim, Player playerAttacker, User victim, DamageCache victimDamageCache, User attacker, DamageCache attackerDamageCache) {
        if (!this.config.rankFarmingProtect) {
            return false;
        }

        Option<Instant> victimTimestamp = victimDamageCache.getLastKillTime(attacker);
        Option<Instant> attackerTimestamp = attackerDamageCache.getLastKillTime(victim);

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

    // This method calculate how many points assisting players should receive
    // Returns a Pair of Set (users that received points for assisting) & List (formatted assists to later use in kill message).
    private Pair<Set<User>, List<String>> calculateAssists(User victim, DamageCache victimDamageCache, User attacker, RankSystem.RankResult result, List<User> messageReceivers) {
        if (!this.config.assistEnable) {
            return Pair.of(new HashSet<>(), new ArrayList<>());
        }

        Set<User> assistUsers = new HashSet<>();
        List<String> assistEntries = new ArrayList<>();

        Map<User, Double> damageMap = victimDamageCache.getSortedTotalDamageMap();

        double toShare = result.getAttackerPoints() * (1 - this.config.assistKillerShare);
        double totalDamage = victimDamageCache.getTotalDamage();

        damageMap.remove(attacker);

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

            assistUsers.add(assistUser);

            PointsChangeEvent assistPointsChangeEvent = new PointsChangeEvent(EventCause.USER, victim, assistUser, addedPoints);
            if (SimpleEventHandler.handle(assistPointsChangeEvent)) {
                assistUser.getRank().updatePoints(currentValue -> currentValue + addedPoints);
            }

            AssistsChangeEvent assistsChangeEvent = new AssistsChangeEvent(EventCause.USER, victim, assistUser, 1);
            if (SimpleEventHandler.handle(assistsChangeEvent)) {
                assistUser.getRank().updateAssists(currentValue -> currentValue + assistsChangeEvent.getAssistsChange());
            }

            messageReceivers.add(assistUser);

            int pointsChange = assistPointsChangeEvent.getPointsChange();
            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{PLAYER}", assistUser.getName())
                    .register("{+}", pointsChange)
                    .register("{PLUS-FORMATTED}", NumberRange.inRangeToString(pointsChange, this.config.killPointsChangeFormat, true))
                    .register("{CHANGE}", Math.abs(pointsChange))
                    .register("{SHARE}", FunnyStringUtils.getPercent(assistFraction));

            assistEntries.add(formatter.format(this.messages.rankAssistEntry));
        }

        return Pair.of(assistUsers, assistEntries);
    }

}
