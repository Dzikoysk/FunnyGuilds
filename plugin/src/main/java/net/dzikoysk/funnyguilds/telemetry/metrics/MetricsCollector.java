package net.dzikoysk.funnyguilds.telemetry.metrics;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SingleLineChart;

import java.util.HashMap;
import java.util.Map;

public class MetricsCollector implements Runnable {

    private final FunnyGuilds plugin;

    private final UserManager userManager;
    private final GuildManager guildManager;

    private MCStats mcstats;
    private Metrics bstats;

    public MetricsCollector(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.guildManager = plugin.getGuildManager();

        try {
            mcstats = new MCStats(plugin);
        } catch (Exception ex) {
            this.mcstats = null;
            FunnyGuilds.getPluginLogger().error("Could not initialize mcstats", ex);
        }

        try {
            this.bstats = new Metrics(plugin, 677);
        } catch (Exception ex) {
            this.bstats = null;
            FunnyGuilds.getPluginLogger().error("Could not initialize bstats", ex);
        }
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, this, 20L);
    }

    @Override
    public void run() {
        // MCStats
        MCStats mcstats = this.mcstats;
        if (mcstats != null) {
            MCStats.Graph global = mcstats.createGraph("Guilds and Users");

            global.addPlotter(new MCStats.Plotter("Users") {
                @Override
                public int getValue() {
                    return userManager.countUsers();
                }
            });

            global.addPlotter(new MCStats.Plotter("Guilds") {
                @Override
                public int getValue() {
                    return guildManager.countGuilds();
                }
            });

            mcstats.start();
        }

        // bStats
        Metrics bstats = this.bstats;
        if (bstats != null) {
            bstats.addCustomChart(new SingleLineChart("users", userManager::countUsers));

            bstats.addCustomChart(new SingleLineChart("guilds", guildManager::countGuilds));

            bstats.addCustomChart(new MultiLineChart("users_and_guilds", () -> {
                Map<String, Integer> valueMap = new HashMap<>();

                valueMap.put("users", userManager.countUsers());
                valueMap.put("guilds", guildManager.countGuilds());

                return valueMap;
            }));
        }
    }
}
