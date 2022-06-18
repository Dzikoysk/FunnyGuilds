package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit extends AbstractFunnyListener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        this.userManager.findByPlayer(player).peek(user -> {
            UserCache cache = user.getCache();

            if (cache.isInCombat()) {
                user.getRank().updateLogouts(currentValue -> currentValue + 1);
            }

            cache.setIndividualPrefix(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.setPlayerList(null);
            cache.clearDamage();

            this.bossBarService.getBossBarProvider(this.funnyServer, user).removeNotification();
        });
    }

}
