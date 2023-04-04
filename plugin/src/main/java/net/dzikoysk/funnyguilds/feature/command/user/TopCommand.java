package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class TopCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.top.name}",
            description = "${user.top.description}",
            aliases = "${user.top.aliases}",
            permission = "funnyguilds.top",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.messageService.getMessage(config -> config.guild.commands.topList)
                .receiver(sender)
                .with(CommandSender.class, receiver -> {
                    User targetUser = this.userManager.findByName(sender.getName()).orNull();
                    return this.rankPlaceholdersService.prepareReplacement(targetUser);
                })
                .send();
    }

}
