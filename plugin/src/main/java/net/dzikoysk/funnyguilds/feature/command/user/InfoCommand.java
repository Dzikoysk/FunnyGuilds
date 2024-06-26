package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InfoCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.info.name}",
            description = "${user.info.description}",
            aliases = "${user.info.aliases}",
            permission = "funnyguilds.info",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        String tag = when(
                Option.when(args.length > 0, () -> args[0])
                        .orElse(Option.of(sender)
                                .is(Player.class)
                                .flatMap(this.userManager::findByPlayer)
                                .filter(User::hasGuild)
                                .flatMap(User::getGuild)
                                .map(Guild::getTag)),
                config -> config.commands.validation.noTagGiven
        );

        Guild guild = GuildValidation.requireGuildByTag(tag);
        this.messageService.getMessage(config -> config.guild.commands.info)
                .receiver(sender)
                .with(this.guildPlaceholdersService.prepareReplacements(sender, guild))
                .send();
    }

}
