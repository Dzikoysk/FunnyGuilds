package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class KickAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.kick.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        User user = UserValidation.requireUserByName(args[0]);

        Guild guild = when(user.getGuild(), config -> config.commands.validation.userHasNoGuild);
        when(user.isOwner(), config -> config.guild.commands.kick.targetIsOwner);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }

        guild.removeMember(user);
        user.removeGuild();
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                .peek(this.plugin::scheduleFunnyTasks);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        this.messageService.getMessage(config -> config.guild.commands.kick.kicked)
                .receiver(sender)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.kick.kickedTarget)
                .receiver(user)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.kick.kickedBroadcast)
                .broadcast()
                .with(formatter)
                .send();
    }

}
