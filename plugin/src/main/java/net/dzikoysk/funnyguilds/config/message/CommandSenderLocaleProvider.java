package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.locale.LocaleProvider;
import java.util.Locale;
import dev.peri.yetanothermessageslibrary.util.BukkitLocaleHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSenderLocaleProvider implements LocaleProvider<CommandSender> {

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return CommandSender.class.isAssignableFrom(clazz);
    }

    @Override
    public @Nullable Locale getLocale(@NotNull CommandSender entity) {
        if (!(entity instanceof Player player)) {
            return null;
        }
        return BukkitLocaleHelper.getLocale(player);
    }

}
