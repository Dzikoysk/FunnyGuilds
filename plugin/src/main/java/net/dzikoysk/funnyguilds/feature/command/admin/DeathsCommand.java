package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeathsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.deaths.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfiguration.generalNoNickGiven);
        when (args.length < 2, this.messageConfiguration.adminNoDeathsGiven);

        int deaths = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            throw new ValidationException(this.messageConfiguration.adminErrorInNumber.replace("{ERROR}", args[1]));
        });

        User admin = AdminUtils.getAdminUser(sender);
        User user = UserValidation.requireUserByName(args[0]);
        UserRank userRank = user.getRank();
        int change = deaths - userRank.getDeaths();

        if (!SimpleEventHandler.handle(new DeathsChangeEvent(AdminUtils.getCause(admin), userRank, admin, change))) {
            return;
        }
        
        userRank.setDeaths(deaths);
        sender.sendMessage(this.messageConfiguration.adminDeathsChanged.replace("{PLAYER}", user.getName()).replace("{DEATHS}", Integer.toString(deaths)));
    }

}
