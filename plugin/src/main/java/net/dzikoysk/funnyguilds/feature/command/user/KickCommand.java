package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class KickCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.kick.name}",
            description = "${user.kick.description}",
            aliases = "${user.kick.aliases}",
            permission = "funnyguilds.kick",
            completer = "members:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@CanManage User deputy, Guild guild, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noNickGiven);
        User formerUser = UserValidation.requireUserByName(args[0]);

        when(!formerUser.hasGuild(), config -> config.commands.validation.userHasNoGuild);
        when(!guild.equals(formerUser.getGuild().get()), config -> config.commands.validation.userNotMember);
        when(formerUser.isOwner(), config -> config.guild.commands.kick.targetIsOwner);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, deputy, guild, formerUser))) {
            return;
        }

        guild.removeMember(formerUser);
        formerUser.removeGuild();
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, formerUser))
                .peek(this.plugin::scheduleFunnyTasks);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.kick.kicked)
                .receiver(deputy)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.kick.kickedTarget)
                .receiver(formerUser)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.kick.kickedBroadcast)
                .broadcast()
                .with(formatter)
                .send();
    }

}
