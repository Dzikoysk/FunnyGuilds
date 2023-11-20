package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class TopPlayerCommand extends TopCommand {

    @FunnyCommand(
            name = "${user.general.top.user.name}",
            description = "${user.general.top.user.description}",
            aliases = "${user.general.top.user.aliases}",
            permission = "funnyguilds.top.user",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.execute(sender, config -> config.player.commands.topList);
    }

}
