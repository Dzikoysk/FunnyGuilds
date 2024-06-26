package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;

public final class GuildsEnabledCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.guilds-enabled.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.config.guildsEnabled = !this.config.guildsEnabled;
        this.messageService
                .getMessage(config -> this.config.guildsEnabled
                        ? config.admin.commands.guild.status.enabled
                        : config.admin.commands.guild.status.disabled
                )
                .receiver(sender)
                .send();
    }

}
