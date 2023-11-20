package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.guild.config.RegionConfiguration;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

public class BucketAction extends AbstractFunnyListener {

    @Inject
    private RegionConfiguration regionConfiguration;

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();

        ProtectionSystem.isProtected(event.getPlayer(), block.getLocation(), true)
                .filterNot(predicate -> this.regionConfiguration.protection.placingBlocksBypass.contains(block.getType()))
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), true)
                .filterNot(predicate -> this.regionConfiguration.protection.placingBlocksBypass.contains(event.getBucket()))
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}
