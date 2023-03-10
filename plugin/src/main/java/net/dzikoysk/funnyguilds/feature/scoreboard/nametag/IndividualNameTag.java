package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import panda.std.Option;

public class IndividualNameTag {

    private final PluginConfiguration pluginConfiguration;

    private WeakReference<Player> playerRef;
    private final User user;

    IndividualNameTag(PluginConfiguration pluginConfiguration, Player player, User user) {
        this.pluginConfiguration = pluginConfiguration;
        this.playerRef = new WeakReference<>(player);
        this.user = user;
    }

    void initialize() {
        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team team = this.prepareTeam(scoreboard, this.user.getName());
        team.setPrefix(this.user.getName());
    }

    private Player getPlayer() {
        Player player = this.playerRef.get();
        if (player == null) {
            player = Bukkit.getPlayer(this.user.getUUID());
            this.playerRef = new WeakReference<>(player);
        }
        return player;
    }

    // Update specific player for this user
    public void updatePlayer(Option<Player> targetPlayerOption, User targetUser) {
        if (targetPlayerOption.isEmpty() || !targetUser.isOnline()) {
            this.removePlayer(targetUser);
            return;
        }
        Player targetPlayer = targetPlayerOption.get();

        FunnyGuilds.getPluginLogger().debug("[NameTag] Updating " + targetUser.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team targetTeam = this.prepareTeam(scoreboard, targetUser.getName());

        ScoreboardConfiguration.NameTag nameTagConfig = this.pluginConfiguration.scoreboard.nametag;
        targetTeam.setPrefix(this.prepareValue(this.prepareConfigValue(nameTagConfig.prefix, targetUser), targetPlayer, targetUser));
        targetTeam.setSuffix(this.prepareValue(this.prepareConfigValue(nameTagConfig.suffix, targetUser), targetPlayer, targetUser));
    }

    public void removePlayer(User target) {
        FunnyGuilds.getPluginLogger().debug("[NameTag] Removing " + target.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to remove NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team team = scoreboard.getEntryTeam(target.getName());
        if (team != null) {
            team.removeEntry(target.getName());
            team.unregister();
        }
    }

    private Team prepareTeam(Scoreboard scoreboard, String teamOwner) {
        Team team = scoreboard.getTeam(teamOwner);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamOwner);
        }

        if (!team.hasEntry(teamOwner)) {
            team.addEntry(teamOwner);
        }

        return team;
    }

    private String prepareValue(RawString rawValue, Player targetPlayer, User targetUser) {
        String value = rawValue.getValue();
        Player player = this.getPlayer();
        if (player == null) {
            return value;
        }

        Guild guild = this.user.getGuild().orNull();
        Guild targetGuild = targetUser.getGuild().orNull();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{REL_TAG}", this.pluginConfiguration.relationalTag.chooseTag(guild, targetGuild))
                .register("{POS}", UserUtils.getUserPosition(this.pluginConfiguration, targetUser));
        value = formatter.format(value);

        String finalValue = value;
        value = GuildPlaceholdersService.getSimplePlaceholders()
                .map(placeholders -> placeholders.formatVariables(targetPlayer, finalValue, targetGuild))
                .orElseGet(value);

        value = HookUtils.replacePlaceholders(targetPlayer, value);
        value = HookUtils.replacePlaceholders(player, targetPlayer, value);

        // Some placeholders may pass color codes (e.g. &6) - we should recolor them
        value = ChatUtils.colored(value);

        return value;
    }

    private RawString prepareConfigValue(ScoreboardConfiguration.NameTag.Value value, User target) {
        Option<Guild> guildOption = this.user.getGuild();
        Option<Guild> targetGuildOption = target.getGuild();

        if (targetGuildOption.isEmpty()) {
            return value.getNoGuild();
        }
        else if (guildOption.isEmpty()) {
            return value.getOtherGuild();
        }

        Guild guild = guildOption.get();
        Guild targetGuild = targetGuildOption.get();

        RawString finalValue = value.getOtherGuild();
        if (guild.equals(targetGuild)) {
            finalValue = value.getOurGuild();
        }
        else if (guild.isAlly(targetGuild) || targetGuild.isAlly(guild)) {
            finalValue = value.getAlliesGuild();
        }
        else if (guild.isEnemy(targetGuild) || targetGuild.isEnemy(guild)) {
            finalValue = value.getEnemiesGuild();
        }
        return finalValue;
    }

}
