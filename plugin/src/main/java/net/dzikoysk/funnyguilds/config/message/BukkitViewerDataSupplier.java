package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.viewer.ViewerDataSupplier;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BukkitViewerDataSupplier implements ViewerDataSupplier<CommandSender> {

    @Override
    public @NotNull Audience getAudience(@NotNull CommandSender commandSender) {
        return commandSender;
    }

    @Override
    public boolean isConsole(@NotNull CommandSender commandSender) {
        return !(commandSender instanceof Player);
    }

    @Override
    public @Nullable UUID getKey(@NotNull CommandSender commandSender) {
        return commandSender instanceof Player player
                ? player.getUniqueId()
                : null;
    }

}
