package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PermissionUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BaseCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.base.name}",
            aliases = "${user.base.aliases}",
            description = "${user.base.description}",
            permission = "funnyguilds.base",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsMember User member, Guild guild) {
        when(!this.config.regionsEnabled, config -> config.guild.region.disabled);
        when(!this.config.baseEnable, config -> config.guild.commands.base.disabled);
        when(member.getCache().getTeleportation() != null, config -> config.guild.commands.base.alreadyTeleporting);

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.base")
                ? Collections.emptyList()
                : this.config.baseItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.guild.commands.base.missingItems)) {
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        Duration time = PermissionUtil.findHighestValue(player, this.config.baseDelay);
        if (time == null || time.isZero()) {
            guild.teleportHome(player);
            this.messageService.getMessage(config -> config.guild.commands.base.teleported)
                    .receiver(member)
                    .send();
            return;
        }

        Location before = player.getLocation();
        Instant teleportStart = Instant.now();
        UserCache cache = member.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }

            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                this.messageService.getMessage(config -> config.guild.commands.base.cancelled)
                        .receiver(member)
                        .send();
                cache.setTeleportation(null);
                player.getInventory().addItem(items);
                return;
            }

            if (Duration.between(teleportStart, Instant.now()).compareTo(time) > 0) {
                cache.getTeleportation().cancel();
                this.messageService.getMessage(config -> config.guild.commands.base.teleported)
                        .receiver(member)
                        .send();
                guild.teleportHome(player);
                cache.setTeleportation(null);
            }
        }, 0L, 10L));

        this.messageService.getMessage(config -> config.guild.commands.base.teleporting)
                .receiver(member)
                .with("{TIME}", time.getSeconds())
                .send();
    }

}
