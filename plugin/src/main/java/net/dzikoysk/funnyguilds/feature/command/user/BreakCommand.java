package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BreakCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.break.name}",
            description = "${user.break.description}",
            aliases = "${user.break.aliases}",
            permission = "funnyguilds.break",
            completer = "allies:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(!guild.hasAllies(), config -> config.guild.commands.breakAlly.noAllies);

        if (args.length < 1) {
            this.messageService.getMessage(config -> config.guild.commands.breakAlly.alliesList)
                    .receiver(owner)
                    .with("{GUILDS}", FunnyStringUtils.join(Entity.names(guild.getAllies()), true))
                    .send();
            return;
        }

        Guild oppositeGuild = GuildValidation.requireGuildByTag(args[0]);
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", oppositeGuild.getName())
                .register("{TAG}", oppositeGuild.getTag());

        when(!guild.isAlly(oppositeGuild), config -> config.guild.commands.validation.notAllied, formatter);

        if (!SimpleEventHandler.handle(new GuildBreakAllyEvent(EventCause.USER, owner, guild, oppositeGuild))) {
            return;
        }

        FunnyFormatter breakFormatter = new FunnyFormatter()
                .register("{GUILD}", oppositeGuild.getName())
                .register("{TAG}", oppositeGuild.getTag());

        FunnyFormatter breakIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        guild.removeAlly(oppositeGuild);
        oppositeGuild.removeAlly(guild);

        this.plugin.getIndividualNameTagManager().peek(manager -> {
            guild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
            oppositeGuild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
        });

        this.messageService.getMessage(config -> config.guild.commands.breakAlly.broke)
                .receiver(owner)
                .with(breakFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.breakAlly.broke)
                .receiver(oppositeGuild.getOwner())
                .with(breakIFormatter)
                .send();
    }

}
