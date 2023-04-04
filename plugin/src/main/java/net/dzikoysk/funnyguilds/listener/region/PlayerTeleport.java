package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class PlayerTeleport extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.userManager.findByUuid(player.getUniqueId())
                .filter(user -> event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN)
                .filterNot(user -> user.hasPermission("funnyguilds.admin.teleport"))
                .filterNot(user -> this.isTeleportationToRegionAllowed(event.getTo(), user))
                .peek(user -> {
                    this.messageService.getMessage(config -> config.guild.region.protection.teleport)
                            .receiver(player)
                            .send();
                    event.setCancelled(true);
                });
    }

    private boolean isTeleportationToRegionAllowed(Location to, User user) {
        Option<Region> regionOption = this.regionManager.findRegionAtLocation(to);
        if (regionOption.isEmpty()) {
            return true;
        }

        Guild guild = regionOption.get().getGuild();
        return guild.isMember(user) || this.isTeleportationToRegionAllowed(guild, user.getGuild().orNull());
    }

    private boolean isTeleportationToRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return this.isTeleportationOnNeutralRegionAllowed(guild, userGuild) &&
                this.isTeleportationOnEnemyRegionAllowed(guild, userGuild) &&
                this.isTeleportationOnAllyRegionAllowed(guild, userGuild);
    }

    private boolean isTeleportationOnNeutralRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.neutral || !guild.isNeutral(userGuild);
    }

    private boolean isTeleportationOnEnemyRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.enemy || !guild.isEnemy(userGuild);
    }

    private boolean isTeleportationOnAllyRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.ally || !guild.isAlly(userGuild);
    }

}
