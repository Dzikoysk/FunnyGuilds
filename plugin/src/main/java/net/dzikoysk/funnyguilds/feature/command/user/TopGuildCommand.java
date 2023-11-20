package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.command.CommandSender;

@FunnyComponent
public class TopGuildCommand extends TopCommand {

    @FunnyCommand(
            name = "${user.general.top.guild.name}",
            description = "${user.general.top.guild.description}",
            aliases = "${user.general.top.guild.aliases}",
            permission = "funnyguilds.top.guild",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.execute(sender, config -> config.guild.commands.topList);
    }

}
