package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.config.ScoreboardConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {

    private final ScoreboardConfiguration scoreboardConfiguration;

    public ScoreboardService(ScoreboardConfiguration scoreboardConfiguration) {
        this.scoreboardConfiguration = scoreboardConfiguration;
    }

    public void updatePlayer(Player player, User user) {
        if (!this.scoreboardConfiguration.enabled) {
            return;
        }

        if (player == null) {
            return;
        }

        UserCache cache = user.getCache();
        cache.getScoreboard().peek(scoreboard -> {
            try {
                player.setScoreboard(scoreboard);
            }
            catch (IllegalStateException e) {
                FunnyGuilds.getPluginLogger().error("[ScoreboardService] Cannot set scoreboard for " + user.getName(), e);
            }
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().debug("We're trying to update player scoreboard, but cached scoreboard is null.");

            Scoreboard scoreboard;
            if (this.scoreboardConfiguration.useSharedScoreboard) {
                scoreboard = player.getScoreboard();
            }
            else {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
            }
            cache.setScoreboard(scoreboard);
        });
    }
}
