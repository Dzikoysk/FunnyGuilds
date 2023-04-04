package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.message.BukkitMessageDispatcher;
import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.viewer.ViewerService;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class FunnyMessageDispatcher extends BukkitMessageDispatcher<FunnyMessageDispatcher> {

    private final Function<User, CommandSender> supplyReceiver;

    public FunnyMessageDispatcher(
            ViewerService<CommandSender> viewerService,
            Function<Object, Locale> localeSupplier,
            Function<Object, Sendable> messageSupplier,
            Function<User, CommandSender> supplyReceiver
    ) {
        super(viewerService, localeSupplier, messageSupplier);
        this.supplyReceiver = supplyReceiver;
    }

    public FunnyMessageDispatcher receiver(@Nullable User user) {
        if (user == null) {
            return this;
        }

        CommandSender sender = this.supplyReceiver.apply(user);
        return this.receiver(sender);
    }

    public FunnyMessageDispatcher receivers(Iterable<User> receivers) {
        receivers.forEach(this::receiver);
        return this;
    }

    public FunnyMessageDispatcher receiver(Guild guild) {
        return this.receivers(guild.getOnlineMembers());
    }

    public FunnyMessageDispatcher receiversIf(boolean condition, Collection<Player> receivers) {
        if (!condition) {
            return this;
        }
        this.receivers(receivers);
        return this;
    }

    public FunnyMessageDispatcher with(Guild guild) {
        return this.with("{TAG}", guild.getTag())
                .with("{NAME}", guild.getName());
    }

}
