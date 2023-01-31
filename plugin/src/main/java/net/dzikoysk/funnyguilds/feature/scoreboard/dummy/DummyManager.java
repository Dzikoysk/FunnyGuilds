package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class DummyManager {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public DummyManager(FunnyGuilds plugin, ScoreboardService scoreboardService) {
        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
        this.scoreboardService = scoreboardService;
    }

    public void updatePlayers() {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(this::updateScore);
    }

    public void updateScore(User user) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(onlineUser -> {
                    this.scoreboardService.updatePlayer(onlineUser);
                    onlineUser.getCache().getDummy().updateScore(user);
                });
    }

}
