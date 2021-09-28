package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class MoveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.move.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, String[] args) {
        when (!this.pluginConfig.regionsEnabled, this.messageConfig.regionsDisabled);
        when (args.length < 1, this.messageConfig.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Location location = player.getLocation();

        if (this.pluginConfig.createCenterY != 0) {
            location.setY(this.pluginConfig.createCenterY);
        }

        int distance = this.pluginConfig.regionSize + this.pluginConfig.createDistance;

        if (this.pluginConfig.enlargeItems != null) {
            distance = this.pluginConfig.enlargeItems.size() * this.pluginConfig.enlargeSize + distance;
        }

        when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), location),
                this.messageConfig.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        when (RegionUtils.isNear(location), this.messageConfig.createIsNear);

        User admin = AdminUtils.getAdminUser(player);
        if (!SimpleEventHandler.handle(new GuildMoveEvent(AdminUtils.getCause(admin), admin, guild, location))) {
            return;
        }
        
        Region region = guild.getRegion();

        if (region == null) {
            region = new Region(guild, location, this.pluginConfig.regionSize);
        } else {
            if (this.pluginConfig.createEntityType != null) {
                GuildEntityHelper.despawnGuildHeart(guild);
            } else if (this.pluginConfig.createMaterial != null && this.pluginConfig.createMaterial.getLeft() != Material.AIR) {
                Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    if (block.getLocation().getBlockY() > 1) {
                        block.setType(Material.AIR);
                    }
                });
            }
            
            region.setCenter(location);
        }
        
        if (this.pluginConfig.createCenterSphere) {
            List<Location> sphere = SpaceUtils.sphere(location, 3, 3, false, true, 0);

            for (Location locationInSphere : sphere) {
                if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                    locationInSphere.getBlock().setType(Material.AIR);
                }
            }
        }

        GuildUtils.spawnHeart(guild);
        player.sendMessage(this.messageConfig.adminGuildRelocated.replace("{GUILD}", guild.getName()).replace("{REGION}", region.getName()));
    }

}
