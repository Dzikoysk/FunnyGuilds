package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class LeaveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.leave.name}",
            description = "${user.leave.description}",
            aliases = "${user.leave.aliases}",
            permission = "funnyguilds.leave",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsMember User member, Guild guild) {
        when(member.isOwner(), config -> config.guild.commands.leave.youAreOwner);

        if (!SimpleEventHandler.handle(new GuildMemberLeaveEvent(EventCause.USER, member, guild, member))) {
            return;
        }

        guild.removeMember(member);
        member.removeGuild();
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, member))
                .peek(this.plugin::scheduleFunnyTasks);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", member.getName());

        this.messageService.getMessage(config -> config.guild.commands.leave.left)
                .receiver(member)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.leave.leftBroadcast)
                .broadcast()
                .with(formatter)
                .send();
    }

}
