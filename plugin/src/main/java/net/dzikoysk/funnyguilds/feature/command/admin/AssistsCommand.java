package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import panda.std.Option;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class AssistsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.assists.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noNickGiven);
        User user = UserValidation.requireUserByName(args[0]);

        when(args.length < 2, config -> config.admin.commands.player.assists.noValueGiven);
        int assists = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            return new InternalValidationException(config -> config.commands.validation.invalidNumber, FunnyFormatter.of("{ERROR}", args[0]));
        });

        User admin = AdminUtils.getAdminUser(sender);
        UserRank userRank = user.getRank();
        int change = assists - userRank.getAssists();
        AssistsChangeEvent assistsChangeEvent = new AssistsChangeEvent(AdminUtils.getCause(admin), admin, user, change);
        if (!SimpleEventHandler.handle(assistsChangeEvent)) {
            return;
        }

        int finalAssists = assists + assistsChangeEvent.getAssistsChange();
        user.getRank().setAssists(finalAssists);

        this.messageService.getMessage(config -> config.admin.commands.player.assists.changed)
                .receiver(sender)
                .with("{PLAYER}", user.getName())
                .with("{VALUE}", finalAssists)
                .send();
    }

}
