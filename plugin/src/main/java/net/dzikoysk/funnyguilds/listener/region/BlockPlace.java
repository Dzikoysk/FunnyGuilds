package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        Location blockLocation = block.getLocation();

        if (type == Material.TNT) {
            if (blockLocation.getBlockY() < this.config.tntProtection.build.minHeight) {
                event.setCancelled(true);
            }

            if (blockLocation.getBlockY() > this.config.tntProtection.build.maxHeight) {
                event.setCancelled(true);
            }
        }

        boolean isProtected = ProtectionSystem.isProtected(player, blockLocation, true)
                .peek(ProtectionSystem::defaultResponse)
                .isPresent();

        if (!isProtected) {
            return;
        }

        if (this.config.placingBlocksBypassOnRegion.contains(type)) {
            return;
        }

        // always cancel to prevent breaking other protection
        // plugins or plugins using BlockPlaceEvent (eg. special ability blocks)
        event.setCancelled(true);
    }

}
