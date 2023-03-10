package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.locale.LocaleProvider;
import dev.peri.yetanothermessageslibrary.util.BukkitLocaleHelper;
import java.util.Locale;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserLocaleProvider implements LocaleProvider<User> {

    private final FunnyServer funnyServer;

    public UserLocaleProvider(FunnyServer funnyServer) {
        this.funnyServer = funnyServer;
    }

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public @Nullable Locale getLocale(@NotNull User entity) {
        return this.funnyServer.getPlayer(entity)
                .map(BukkitLocaleHelper::getLocale)
                .orNull();
    }

}
