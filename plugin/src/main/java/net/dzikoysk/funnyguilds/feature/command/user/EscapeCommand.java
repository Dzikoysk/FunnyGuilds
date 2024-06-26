package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import panda.std.Option;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class EscapeCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.escape.name}",
            description = "${user.escape.description}",
            aliases = "${user.escape.aliases}",
            permission = "funnyguilds.escape",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user) {
        when(!this.config.regionsEnabled, config -> config.guild.region.disabled);
        when(!this.config.escapeEnable || !this.config.baseEnable, config -> config.guild.commands.escape.disabled);
        when(user.getCache().getTeleportation() != null, config -> config.guild.commands.escape.alreadyEscaping);

        Location playerLocation = player.getLocation();
        Option<Region> regionOption = this.regionManager.findRegionAtLocation(playerLocation);
        when(regionOption.isEmpty(), config -> config.guild.commands.escape.notInRegion);

        Region region = regionOption.get();
        Duration time = this.config.escapeDelay;

        if (!user.hasGuild()) {
            when(!this.config.escapeSpawn, config -> config.guild.commands.escape.noGuild);
            this.scheduleTeleportation(player, user, player.getWorld().getSpawnLocation(), time, () -> {});
            return;
        }

        Guild guild = user.getGuild().get();
        when(guild.equals(region.getGuild()), config -> config.guild.commands.escape.yourRegion);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{TIME}", time.getSeconds())
                .register("{PLAYER}", player.getName())
                .register("{X}", playerLocation.getBlockX())
                .register("{Y}", playerLocation.getBlockY())
                .register("{Z}", playerLocation.getBlockZ());

        if (time.getSeconds() >= 1) {
            this.messageService.getMessage(config -> config.guild.commands.escape.escaping)
                    .receiver(player)
                    .with(formatter)
                    .send();
            this.messageService.getMessage(config -> config.guild.commands.escape.escapingOpponents)
                    .receiver(region.getGuild())
                    .with(formatter)
                    .send();
        }

        guild.getHome().peek(home -> this.scheduleTeleportation(player, user, home, time, () -> {
            this.messageService.getMessage(config -> config.guild.commands.escape.escaped)
                    .receiver(player)
                    .send();
            this.messageService.getMessage(config -> config.guild.commands.escape.escapedOpponents)
                    .receiver(region.getGuild())
                    .with(formatter)
                    .send();
        }));
    }

    private void scheduleTeleportation(Player player, User user, Location destination, Duration time, Runnable onSuccess) {
        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(0);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }

            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                this.messageService.getMessage(config -> config.guild.commands.escape.cancelled)
                        .receiver(player)
                        .send();
                cache.setTeleportation(null);
                return;
            }

            if (timeCounter.getAndIncrement() > time.getSeconds()) {
                cache.getTeleportation().cancel();
                player.teleport(destination);
                onSuccess.run();
                cache.setTeleportation(null);
            }
        }, 0L, (time.toMillis() < 1) ? 0L : 20L));
    }

}