package net.dzikoysk.funnyguilds.listener.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void preNormalExplosionHandler(EntityExplodeEvent event) {
        Entity explosionEntity = event.getEntity();

        List<Block> explodedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        Map<Material, Double> explosiveMaterials = this.config.explodeMaterials;
        List<Block> blocksInSphere = SpaceUtils.sphereBlocks(
                explodeLocation,
                this.config.explodeRadius,
                this.config.explodeRadius,
                0,
                false,
                true
        );

        explodedBlocks.removeIf(block -> {
            int height = block.getLocation().getBlockY();
            return height < this.config.tntProtection.explode.minHeight || height > this.config.tntProtection.explode.maxHeight;
        });

        blocksInSphere.removeIf(block -> {
            int height = block.getLocation().getBlockY();
            return height < this.config.tntProtection.explode.minHeight || height > this.config.tntProtection.explode.maxHeight;
        });

        if (this.config.explodeShouldAffectOnlyGuild) {
            explodedBlocks.removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation())
                    .filterNot(region -> region.getGuild() == null)
                    .filter(region -> block.getType() != Material.TNT)
                    .isEmpty());

            blocksInSphere.removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation())
                    .filterNot(region -> region.getGuild() == null)
                    .isEmpty());
        }

        this.regionManager.findRegionAtLocation(explodeLocation).peek(region -> {
            Guild guild = region.getGuild();

            if (this.config.warTntProtection && !guild.canBeAttacked()) {
                event.setCancelled(true);

                if (explosionEntity instanceof TNTPrimed) {
                    TNTPrimed entityTnt = (TNTPrimed) explosionEntity;
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        this.messageService.getMessage(config -> config.guild.region.explosion.hasProtection)
                                .receiver(explosionSource)
                                .send();
                    }
                }

                return;
            }

            region.getHeart().peek(heart -> {
                explodedBlocks.removeIf(block -> block.getLocation().equals(heart));
                blocksInSphere.removeIf(block -> block.getLocation().equals(heart));
            });
        });

        if (this.config.warTntProtection) {
            // Remove block if protected
            boolean anyBlockRemovedInSphere = blocksInSphere.removeIf(block ->
                    this.regionManager.findRegionAtLocation(block.getLocation())
                            .map(Region::getGuild)
                            .filterNot(Guild::canBeAttacked)
                            .isPresent());
            boolean anyBlockRemovedInExplosion = explodedBlocks.removeIf(block ->
                    this.regionManager.findRegionAtLocation(block.getLocation())
                            .map(Region::getGuild)
                            .filterNot(Guild::canBeAttacked)
                            .isPresent());

            if (anyBlockRemovedInSphere || anyBlockRemovedInExplosion) {
                if (explosionEntity instanceof TNTPrimed) {
                    TNTPrimed entityTnt = (TNTPrimed) explosionEntity;
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        this.messageService.getMessage(config -> config.guild.region.explosion.hasProtection)
                                .receiver(explosionSource)
                                .send();
                    }
                }
            }
        }

        List<Block> additionalExplodedBlocks = new ArrayList<>();
        for (Block block : blocksInSphere) {
            if (block.getType() == Material.TNT) {
                // We want to preserve TNT chain explosions, see GH-1414.
                continue;
            }

            Material material = block.getType();
            Double explodeChance = explosiveMaterials.get(material);

            if (explodeChance == null) {
                if (!this.config.allMaterialsAreExplosive) {
                    continue;
                }

                explodeChance = this.config.defaultExplodeChance;
            }

            if (SpaceUtils.chance(explodeChance)) {
                additionalExplodedBlocks.add(block);
            }
        }

        if (!SimpleEventHandler.handle(new GuildEntityExplodeEvent(FunnyEvent.EventCause.UNKNOWN, additionalExplodedBlocks))) {
            event.setCancelled(true);
            return;
        }

        additionalExplodedBlocks.stream()
                .filter(block -> !explodedBlocks.contains(block))
                .forEach(explodedBlocks::add);
    }

}
