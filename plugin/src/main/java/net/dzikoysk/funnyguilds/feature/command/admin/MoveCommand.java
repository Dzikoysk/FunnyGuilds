package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class MoveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.move.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User admin, String[] args) {
        when(!this.config.regionsEnabled, config -> config.guild.region.disabled);
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        HeartConfiguration heartConfig = this.config.heart;
        Location location = player.getLocation().getBlock().getLocation();
        World world = player.getWorld();

        heartConfig.center.prepareCenterLocation(location);

        if (heartConfig.createEntityType != null && location.getBlockY() < (world.getMaxHeight() - 2)) {
            location.setY(location.getBlockY() + 2);
        }

        int distance = this.config.regionSize + this.config.createDistance;
        if (this.config.enlargeItems != null) {
            distance = this.config.enlargeItems.size() * this.config.enlargeSize + distance;
        }

        when(distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), location),
                config -> config.guild.commands.create.nearSpawn, FunnyFormatter.of("{DISTANCE}", distance));
        when(this.regionManager.isNearRegion(location), config -> config.guild.commands.create.nearOtherGuild);

        if (!SimpleEventHandler.handle(new GuildMoveEvent(AdminUtils.getCause(admin), admin, guild, location))) {
            return;
        }

        Region region = guild.getRegion()
                .peek(peekRegion -> {
                    if (heartConfig.createEntityType != null) {
                        this.plugin.getGuildEntityHelper().despawnGuildEntity(guild);
                    }
                    else if (heartConfig.createMaterial != null && heartConfig.createMaterial != Material.AIR) {
                        peekRegion.getHeartBlock()
                                .filter(heart -> heart.getLocation().getBlockY() > 1)
                                .peek(heart -> Bukkit.getScheduler().runTask(this.plugin, () -> heart.setType(Material.AIR)));
                    }

                    this.regionManager.moveRegionCenter(peekRegion, location);
                    guild.getEnderCrystal()
                            .map(Location::clone)
                            .map(homeLocation -> homeLocation.subtract(0.0D, 1.0D, 0.0D))
                            .peek(guild::setHome);
                })
                .orElseGet(() -> new Region(guild, location, this.config.regionSize));

        if (heartConfig.createCenterSphere) {
            SpaceUtils.sphere(location, 3, 3, false, true, 0).stream()
                    .map(Location::getBlock)
                    .filter(block -> block.getType() != Material.BEDROCK)
                    .forEach(block -> block.setType(Material.AIR));
        }

        this.plugin.getGuildEntityHelper().spawnGuildEntity(guild);
        this.messageService.getMessage(config -> config.admin.commands.guild.move.moved)
                .receiver(player)
                .with(guild)
                .send();
    }

}
