package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Set;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.annotations.Inject;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class AllyCommand extends AbstractFunnyCommand {

    @Inject
    public AllyInvitationList allyInvitationList;

    @FunnyCommand(
            name = "${user.ally.name}",
            description = "${user.ally.description}",
            aliases = "${user.ally.aliases}",
            permission = "funnyguilds.ally",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when(invitations.isEmpty(), config -> config.guild.commands.ally.noInvitations);
            String guildNames = FunnyStringUtils.join(this.allyInvitationList.getInvitationGuildNames(guild), true);

            this.messageService.getMessage(config -> config.guild.commands.ally.invitationsList)
                    .receiver(owner)
                    .with("{GUILDS}", guildNames)
                    .send();

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        User invitedOwner = invitedGuild.getOwner();

        when(guild.equals(invitedGuild), config -> config.guild.commands.ally.yourGuild);
        when(guild.isAlly(invitedGuild), config -> config.guild.commands.ally.alreadyAllied);

        if (guild.isEnemy(invitedGuild)) {
            this.endWar(owner, invitedOwner, guild, invitedGuild);
        }

        when(guild.getAllies().size() >= this.config.maxAlliesBetweenGuilds,
                config -> config.guild.commands.ally.alliesLimit, FunnyFormatter.of("{AMOUNT}", this.config.maxAlliesBetweenGuilds));

        if (invitedGuild.getAllies().size() >= this.config.maxAlliesBetweenGuilds) {
            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", this.config.maxAlliesBetweenGuilds);

            this.messageService.getMessage(config -> config.guild.commands.ally.targetAlliesLimit)
                    .receiver(owner)
                    .with(formatter)
                    .send();
            return;
        }

        if (this.allyInvitationList.hasInvitation(invitedGuild, guild)) {
            this.acceptInvitation(owner, invitedOwner, guild, invitedGuild);
            return;
        }

        if (this.allyInvitationList.hasInvitation(guild, invitedGuild)) {
            this.revokeInvitation(owner, invitedOwner, guild, invitedGuild);
            return;
        }

        this.invite(owner, invitedOwner, guild, invitedGuild);
    }

    private void endWar(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
        guild.removeEnemy(invitedGuild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.enemy.enemyEnd)
                .receiver(owner)
                .with(allyFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.enemy.enemyEndTarget)
                .receiver(invitedOwner)
                .with(allyIFormatter)
                .send();
    }

    private void acceptInvitation(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
        if (!SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
            return;
        }

        this.allyInvitationList.expireInvitation(invitedGuild, guild);

        guild.addAlly(invitedGuild);
        invitedGuild.addAlly(guild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.ally.allied)
                .receiver(owner)
                .with(allyFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.ally.alliedTarget)
                .receiver(invitedOwner)
                .with(allyIFormatter)
                .send();

        this.plugin.getIndividualNameTagManager().peek(manager -> {
            guild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
            invitedGuild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
        });
    }

    private void revokeInvitation(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
        if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
            return;
        }

        this.allyInvitationList.expireInvitation(guild, invitedGuild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.ally.allyInviteReturn)
                .receiver(owner)
                .with(allyFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.ally.allyInviteReturnTarget)
                .receiver(invitedOwner)
                .with(allyIFormatter)
                .send();
    }

    private void invite(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
            return;
        }

        this.allyInvitationList.createInvitation(guild, invitedGuild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.ally.allyInvite)
                .receiver(owner)
                .with(allyFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.ally.allyInviteTarget)
                .receiver(invitedOwner)
                .with(allyIFormatter)
                .send();
    }

}
