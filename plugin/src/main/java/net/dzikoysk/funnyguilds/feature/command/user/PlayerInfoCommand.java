package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.function.Function;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class PlayerInfoCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.player.name}",
            description = "${user.player.description}",
            aliases = "${user.player.aliases}",
            permission = "funnyguilds.playerinfo",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length == 0 && !(sender instanceof Player), config -> config.commands.validation.playerOnly);

        String name = args.length == 0 ? sender.getName() : args[0];
        User user = when(this.userManager.findByName(name, this.config.playerLookupIgnorecase), config -> config.commands.validation.notPlayedBefore);

        this.sendInfoMessage(config -> config.player.commands.info.longForm, user, sender);
    }

    public void sendInfoMessage(Function<MessageConfiguration, Sendable> baseMessage, User infoUser, CommandSender messageTarget) {
        this.messageService.getMessage(baseMessage)
                .receiver(messageTarget)
                .with(this.userPlaceholdersService.prepareReplacements(messageTarget, infoUser))
                .with("{RANK}", infoUser.getRank().getPosition(DefaultTops.USER_POINTS_TOP))
                .with(CommandSender.class, receiver -> {
                    FunnyFormatter guildFormatter = new FunnyFormatter();
                    if (infoUser.hasGuild()) {
                        Guild guild = infoUser.getGuild().get();
                        guildFormatter.register("{NAME}", guild.getName());
                        guildFormatter.register("{TAG}", guild.getTag());
                    } else {
                        guildFormatter.register("{NAME}", this.messageService.<String>get(receiver, config -> config.noValue.guild.name));
                        guildFormatter.register("{TAG}", this.messageService.<String>get(receiver, config -> config.noValue.guild.tag));
                    }
                    return guildFormatter;
                })
                .send();
    }

}
