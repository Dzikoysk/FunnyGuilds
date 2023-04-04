package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class RankingCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.ranking.name}",
            description = "${user.ranking.description}",
            aliases = "${user.ranking.aliases}",
            permission = "funnyguilds.ranking",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.messageService.getMessage(config -> config.player.commands.topList)
                .receiver(sender)
                .with(CommandSender.class, receiver -> {
                    User targetUser = this.userManager.findByName(sender.getName()).orNull();
                    return this.rankPlaceholdersService.prepareReplacement(targetUser);
                })
                .send();
    }

}
