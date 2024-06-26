package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class LeaderCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.leader.name}",
            description = "${user.leader.description}",
            aliases = "${user.leader.aliases}",
            permission = "funnyguilds.leader",
            completer = "members:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noNickGiven);
        User leaderUser = UserValidation.requireUserByName(args[0]);

        when(owner.equals(leaderUser), config -> config.guild.commands.leader.leaderMustBeDifferent);
        when(!guild.isMember(leaderUser), config -> config.commands.validation.userNotMember);

        if (!SimpleEventHandler.handle(new GuildMemberLeaderEvent(EventCause.USER, owner, guild, leaderUser))) {
            return;
        }

        guild.setOwner(leaderUser);

        this.messageService.getMessage(config -> config.guild.commands.leader.changed)
                .receiver(owner)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.leader.changedTarget)
                .receiver(leaderUser)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.leader.changedMembers)
                .receiver(guild)
                .send();
    }

}
