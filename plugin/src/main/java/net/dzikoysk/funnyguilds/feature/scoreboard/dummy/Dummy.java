package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.config.ScoreboardConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import panda.std.Option;

public class Dummy {

    private static final String OBJECTIVE_NAME = "FG-Points";

    private final ScoreboardConfiguration scoreboardConfiguration;
    private final User user;

    Dummy(ScoreboardConfiguration scoreboardConfiguration, User user) {
        this.scoreboardConfiguration = scoreboardConfiguration;
        this.user = user;
    }

    @SuppressWarnings("deprecation")
    Option<Objective> initialize() {
        if (this.user.hasPermission("funnyguilds.admin.disabledummy")) {
            return Option.none();
        }

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize Dummy, but scoreboard hasn't been initialized.");
            return Option.none();
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            return Option.of(objective);
        }

        objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(this.scoreboardConfiguration.dummy.suffix);

        return Option.of(objective);
    }

    // Update specific player for this user
    public void updatePlayer(User target) {
        if (target.hasPermission("funnyguilds.admin.disabledummy")) {
            return;
        }

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update Dummy score, but scoreboard hasn't been initialized.");
            return;
        }
        scoreboardOption
                .map(scoreboard -> scoreboard.getObjective(OBJECTIVE_NAME))
                .orElse(this::initialize)
                .peek(objective -> objective.getScore(target.getName()).setScore(target.getRank().getPoints()));
    }

}
