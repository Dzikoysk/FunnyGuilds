package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EntityExplode implements Listener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void preNormalExplosionHandler(EntityExplodeEvent event) {
        List<Block> destroyedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Map<Material, Double> explosiveMaterials = config.explodeMaterials;

        List<Block> blocksInSphere = SpaceUtils.sphereBlocks(
                explodeLocation,
                config.explodeRadius,
                config.explodeRadius,
                0,
                false,
                true
        );

        blocksInSphere.stream()
                .filter(block -> config.allMaterialsAreExplosive || (explosiveMaterials.containsKey(block.getType())))
                .filter(block -> ! destroyedBlocks.contains(block))
                .forEach(destroyedBlocks::add);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void postNormalExplosionHandler(EntityExplodeEvent event) {
        List<Block> explodedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        Map<Material, Double> explosiveMaterials = config.explodeMaterials;

        if (config.explodeShouldAffectOnlyGuild) {
            explodedBlocks.removeIf(block -> {
                Region region = RegionUtils.getAt(block.getLocation());

                return (region == null || region.getGuild() == null) && block.getType() != Material.TNT;
            });
        }

        Region region = RegionUtils.getAt(explodeLocation);

        if (region != null) {
            Guild guild = region.getGuild();

            if (config.warTntProtection && ! guild.canBeAttacked()) {
                event.setCancelled(true);
                return;
            }

            Location guildHeartLocation = region.getHeart();
            explodedBlocks.removeIf(block -> block.getLocation().equals(guildHeartLocation));
            guild.setBuild(System.currentTimeMillis() + config.regionExplode * 1000L);

            for (User user : guild.getMembers()) {
                Player player = user.getPlayer();

                if (player != null && !informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                    player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionExplode.replace("{TIME}", Integer.toString(config.regionExplode)));
                }
            }
        }

        if (config.warTntProtection) {
            explodedBlocks.removeIf(block -> {
                Region regionAtExplosion = RegionUtils.getAt(block.getLocation());
                return regionAtExplosion != null && regionAtExplosion.getGuild() != null && ! regionAtExplosion.getGuild().canBeAttacked();
            });
        }

        List<Block> filteredExplodedBlocks = new ArrayList<>();

        for (Block explodedBlock : explodedBlocks) {
            Material material = explodedBlock.getType();
            Double explodeChance = explosiveMaterials.get(material);

            if (explodeChance == null) {
                if (!config.allMaterialsAreExplosive) {
                    continue;
                }

                explodeChance = config.defaultExplodeChance;
            }

            if (SpaceUtils.chance(explodeChance)) {
                filteredExplodedBlocks.add(explodedBlock);
            }
        }

        if (!SimpleEventHandler.handle(new GuildEntityExplodeEvent(FunnyEvent.EventCause.UNKNOWN, filteredExplodedBlocks))) {
            event.setCancelled(true);
            return;
        }

        for (Block explodedBlock : filteredExplodedBlocks) {
            Material material = explodedBlock.getType();

            if (material == Material.WATER || material == Material.LAVA) {
                explodedBlock.setType(Material.AIR);
            }
            else {
                explodedBlock.breakNaturally();
            }
        }
    }

}
