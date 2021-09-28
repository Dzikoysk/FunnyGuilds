package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

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
        UserManager userManager = this.userManager;
        String tag = Option.when(args.length > 0, () -> args[0])
                .orElse(Option.of(sender)
                        .is(Player.class)
                        .flatMap(userManager::getUser)
                        .filter(User::hasGuild)
                        .map(User::getGuild)
                        .map(Guild::getTag))
                .orThrow(() -> new ValidationException(messageConfiguration.infoTag));

        Guild guild = GuildValidation.requireGuildByTag(tag);
        Formatter formatter = Placeholders.GUILD_ALL.toFormatter(guild);

        for (String messageLine : messageConfiguration.infoList) {
            sender.sendMessage(formatter.format(messageLine));
        }
    }

}
