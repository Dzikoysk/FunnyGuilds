package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;

public final class MainCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.main.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.messageService.getMessage(config -> config.admin.commands.help)
                .receiver(sender)
                .send();
    }

}
