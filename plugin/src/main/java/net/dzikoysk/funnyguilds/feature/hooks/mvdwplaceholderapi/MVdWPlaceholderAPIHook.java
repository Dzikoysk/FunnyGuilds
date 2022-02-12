package net.dzikoysk.funnyguilds.feature.hooks.mvdwplaceholderapi;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import panda.utilities.StringUtils;

public class MVdWPlaceholderAPIHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public MVdWPlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        PluginConfiguration pluginConfiguration = this.plugin.getPluginConfiguration();
        UserManager userManager = this.plugin.getUserManager();
        RankManager rankManager = this.plugin.getRankManager();

        DefaultTablistVariables.getFunnyVariables().forEach((id, variable) ->
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_" + id, event -> {
                    OfflinePlayer target = event.getOfflinePlayer();
                    if (target == null) {
                        return StringUtils.EMPTY;
                    }

                    return userManager.findByUuid(target.getUniqueId())
                            .map(variable::get)
                            .orElseGet("none");
                }));

        Map<String, UserTop> userTopMap = rankManager.getUserTopMap();
        Map<String, GuildTop> guildTopMap = rankManager.getGuildTopMap();

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int position = i;

            userTopMap.forEach((id, top) ->
                    PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + id + "-" + position, event -> {
                        User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                        return RankUtils.parseTop(user, "{PTOP-" + id.toUpperCase() + "-" + position + "}");
                    }));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + position, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseRank(user, "{PTOP-" + position + "}");
                });
            }
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int position = i;

            guildTopMap.forEach((id, top) ->
                    PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + id + "-" + position, event -> {
                        User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                        return RankUtils.parseTop(user, "{GTOP-" + id.toUpperCase() + "-" + position + "}");
                    }));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + position, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseRank(user, "{GTOP-" + position + "}");
                });
            }
        }

        userTopMap.forEach((id, top) ->
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_position-" + id, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseTopPosition(user, "{POSITION-" + id.toUpperCase() + "}");
                }));

        guildTopMap.forEach((id, top) ->
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_g-position-" + id, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseTopPosition(user, "{G-POSITION-" + id.toUpperCase() + "}");
                }));

        return HookInitResult.SUCCESS;
    }

    public String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }

}
