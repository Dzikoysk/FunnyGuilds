package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import panda.std.Option;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class KillsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.kills.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noNickGiven);
        User user = UserValidation.requireUserByName(args[0]);

        when(args.length < 2, config -> config.admin.commands.player.kills.noValueGiven);
        int kills = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            return new InternalValidationException(config -> config.commands.validation.invalidNumber, FunnyFormatter.of("{ERROR}", args[0]));
        });

        User admin = AdminUtils.getAdminUser(sender);
        UserRank userRank = user.getRank();
        int change = kills - userRank.getKills();
        KillsChangeEvent killsChangeEvent = new KillsChangeEvent(AdminUtils.getCause(admin), admin, user, change);
        if (!SimpleEventHandler.handle(killsChangeEvent)) {
            return;
        }

        int finalKills = user.getRank().getKills() + killsChangeEvent.getKillsChange();
        user.getRank().setKills(finalKills);

        this.messageService.getMessage(config -> config.admin.commands.player.kills.changed)
                .receiver(sender)
                .with("{PLAYER}", user.getName())
                .with("{VALUE}", finalKills)
                .send();
    }

}
