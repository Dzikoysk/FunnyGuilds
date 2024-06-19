package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.LogoutsChangeEvent;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

public class PlayerQuit extends AbstractFunnyListener {

    @Inject
    private DamageManager damageManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        this.handleQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        this.handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        this.userManager.findByUuid(player.getUniqueId()).peek(user -> {
            UserCache cache = user.getCache();
            DamageState damageState = damageManager.getDamageState(user.getUUID());

            if (damageState.isInCombat()) {
                LogoutsChangeEvent logoutsChangeEvent = new LogoutsChangeEvent(FunnyEvent.EventCause.USER, user, user, 1);

                if (SimpleEventHandler.handle(logoutsChangeEvent)) {
                    user.getRank().updateLogouts(currentValue -> currentValue + logoutsChangeEvent.getLogoutsChange());
                }
            }

            this.plugin.getIndividualNameTagManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .peek(this.plugin::scheduleFunnyTasks);
            this.plugin.getDummyManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .peek(this.plugin::scheduleFunnyTasks);

            cache.setIndividualNameTag(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.setPlayerList(null);
            damageState.clear();
        });

        this.messageService.playerQuit(player);
    }

}
