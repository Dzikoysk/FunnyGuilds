package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

public class PlayerMove extends AbstractFunnyListener {

    @Inject
    public GuildEntityHelper guildEntityHelper;

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        this.onMove(event); // We have to manually call onMove when player teleports - in other case the move event won't be called
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();
        if (to == null) {
            return;
        }

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Option<User> userOption = this.userManager.findByPlayer(player);
            if (userOption.isEmpty()) {
                return;
            }

            User user = userOption.get();
            UserCache cache = user.getCache();

            Option<Region> regionOptionFrom = this.regionManager.findRegionAtLocation(from);
            Option<Region> regionOptionTo = this.regionManager.findRegionAtLocation(to);

            if (regionOptionFrom.equals(regionOptionTo)) {
                return;
            }

            regionOptionFrom
                    .map(Region::getGuild)
                    .peek(guild -> {
                        this.logger.debug(String.format("Player %s left region of guild %s", player.getName(), guild.getName()));

                        if (!SimpleEventHandler.handle(new GuildRegionLeaveEvent(EventCause.USER, user, guild))) {
                            event.setCancelled(true);
                            return;
                        }

                        FunnyFormatter formatter = new FunnyFormatter()
                                .register("{GUILD}", guild.getName())
                                .register("{TAG}", guild.getTag());

                        this.messageService.getMessage(config -> config.notificationLeaveGuildRegion)
                                .with(formatter)
                                .receiver(player)
                                .send();
                    });

            regionOptionTo
                    .map(Region::getGuild)
                    .peek(guild -> {
                        this.logger.debug(String.format("Player %s entered region of guild %s", player.getName(), guild.getName()));

                        if (!SimpleEventHandler.handle(new GuildRegionEnterEvent(EventCause.USER, user, guild))) {
                            event.setCancelled(true);
                            return;
                        }

                        if (this.config.heart.createEntityType != null) {
                            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.guildEntityHelper.spawnGuildEntity(guild, player), 40L);
                        }

                        FunnyFormatter formatter = new FunnyFormatter()
                                .register("{GUILD}", guild.getName())
                                .register("{TAG}", guild.getTag())
                                .register("{PLAYER}", player.getName());

                        this.messageService.getMessage(config -> config.notificationEnterGuildRegion)
                                .with(formatter)
                                .receiver(player)
                                .send();

                        if (player.hasPermission("funnyguilds.admin.notification")) {
                            return;
                        }

                        if (cache.getNotificationTime() > 0 && System.currentTimeMillis() < cache.getNotificationTime()) {
                            return;
                        }

                        if (!this.config.regionEnterNotificationGuildMember && user.hasGuild() &&
                                guild.getTag().equals(user.getGuild().get().getTag())) {
                            return;
                        }

                        this.messageService.getMessage(config -> config.notificationIntruderEnterGuildRegion)
                                .with(formatter)
                                .receiver(guild)
                                .send();

                        cache.setNotificationTime(System.currentTimeMillis() + 1000L * this.config.regionNotificationCooldown);
                    });
        });
    }

}
