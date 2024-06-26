package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntProtection extends AbstractFunnyListener {

    private final Cooldown<UUID> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (this.tntCanExplode()) {
            return;
        }

        if (this.config.tntProtection.time.enabledGlobal) {
            event.setCancelled(true);
            return;
        }

        if (this.config.tntProtection.time.enabled) {
            if (this.regionManager.findRegionAtLocation(event.getLocation()).isPresent()) {
                event.setCancelled(true);
                return;
            }

            event.blockList().removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation()).isPresent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        Set<Player> players = this.regionManager.findRegionAtLocation(event.getLocation())
                .map(Region::getGuild)
                .filterNot(guild -> this.config.warTntProtection && !this.config.regionExplodeBlockProtected && !guild.canBeAttacked())
                .filterNot(guild -> !this.config.regionExplodeBlockTntDisabled && !this.tntCanExplode())
                .filterNot(guild -> this.config.regionExplodeExcludeEntities.contains(event.getEntityType()))
                .peek(guild -> guild.setBuild(Instant.now().plus(this.config.regionExplode)))
                .toStream(guild -> guild.getMembers().stream())
                .filterNot(user -> this.informationMessageCooldowns.cooldown(user.getUUID(), this.config.infoPlayerCooldown))
                .flatMap(user -> this.funnyServer.getPlayer(user))
                .toSet();

        this.messageService.getMessage( config -> config.guild.region.explosion.message)
                .with("{TIME}", this.config.regionExplode.getSeconds())
                .receivers(players)
                .send();
    }

    private boolean tntCanExplode() {
        if (!this.config.tntProtection.time.enabled && !this.config.tntProtection.time.enabledGlobal) {
            return true;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = this.config.tntProtection.time.startTime.getTime();
        LocalTime end = this.config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = this.config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        return !isWithinTimeframe;
    }

}
