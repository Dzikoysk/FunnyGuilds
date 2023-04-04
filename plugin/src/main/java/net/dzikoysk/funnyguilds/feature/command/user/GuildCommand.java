package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class GuildCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.guild.name}",
            description = "${user.guild.description}",
            aliases = "${user.guild.aliases}",
            permission = "funnyguilds.guild",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.messageService.getMessage(config -> config.guild.commands.help)
                .receiver(sender)
                .send();
    }

}
