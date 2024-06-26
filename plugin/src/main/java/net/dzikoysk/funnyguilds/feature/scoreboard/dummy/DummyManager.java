package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.AbstractScoreboardHandler;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.config.ScoreboardConfiguration;
import org.bukkit.entity.Player;

public class DummyManager extends AbstractScoreboardHandler<Dummy> {

    public DummyManager(ScoreboardConfiguration scoreboardConfiguration, UserManager userManager, ScoreboardService scoreboardService) {
        super(scoreboardConfiguration, userManager, scoreboardService);
    }

    @Override
    protected Dummy getOrCreateData(Player player, User user) {
        UserCache userCache = user.getCache();
        return userCache.getDummy().orElseGet(() -> {
            // Ensure user has their own scoreboard
            this.scoreboardService.updatePlayer(player, user);

            Dummy dummy = new Dummy(this.scoreboardConfiguration, user);
            dummy.initialize();
            userCache.setDummy(dummy);
            return dummy;
        });
    }

    @Override
    protected void update(AbstractScoreboardHandler<Dummy>.UpdateData observerData, AbstractScoreboardHandler<Dummy>.UpdateData targetData) {
        observerData.getData().peek(dummy -> dummy.updatePlayer(targetData.getUser()));
        targetData.getData().peek(dummy -> dummy.updatePlayer(observerData.getUser()));
    }


}
