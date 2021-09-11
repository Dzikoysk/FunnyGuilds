package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Iterator;

public class TntProtection implements Listener {

    private final FunnyGuilds plugin;

    public TntProtection(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();

        if (!config.guildTNTProtectionEnabled && !config.guildTNTProtectionGlobal) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = config.guildTNTProtectionStartTime;
        LocalTime end = config.guildTNTProtectionEndTime;

        boolean isWithinTimeframe = config.guildTNTProtectionPassingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        if (!isWithinTimeframe) {
            return;
        }

        if (config.guildTNTProtectionGlobal) {
            event.setCancelled(true);
            return;
        }

        if (config.guildTNTProtectionEnabled) {
            Region region = RegionUtils.getAt(event.getLocation());

            if (region != null) {
                event.setCancelled(true);
                return;
            }

            Iterator<Block> affectedBlocks = event.blockList().iterator();

            while (affectedBlocks.hasNext()) {
                Block block = affectedBlocks.next();
                Region maybeRegion = RegionUtils.getAt(block.getLocation());

                if (maybeRegion != null) {
                    affectedBlocks.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        Location explosionLocation = event.getLocation();
        Region region = RegionUtils.getAt(explosionLocation);

        if (region != null) {
            region.getGuild().setBuild(Instant.now().plusSeconds(config.regionExplode).toEpochMilli());
        }
    }

}
