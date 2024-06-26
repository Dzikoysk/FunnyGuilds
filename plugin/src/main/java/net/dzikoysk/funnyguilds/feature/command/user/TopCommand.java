package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

public abstract class TopCommand extends AbstractFunnyCommand {

    protected void execute(CommandSender sender, Function<MessageConfiguration, Sendable> message) {
        this.messageService.getMessage(message)
                .receiver(sender)
                .with(CommandSender.class, receiver -> {
                    User targetUser = this.userManager.findByName(sender.getName()).orNull();
                    return this.rankPlaceholdersService.prepareReplacement(targetUser);
                })
                .send();
    }

}
