package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class WarCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.war.name}",
            description = "${user.war.description}",
            aliases = "${user.war.aliases}",
            permission = "funnyguilds.war",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(args.length < 1, config -> config.guild.commands.enemy.correctUsage);
        Guild enemyGuild = GuildValidation.requireGuildByTag(args[0]);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag())
                .register("{AMOUNT}", this.config.maxEnemiesBetweenGuilds);

        when(guild.equals(enemyGuild), config -> config.guild.commands.enemy.yourGuild);
        when(guild.isAlly(enemyGuild), config -> config.guild.commands.enemy.targetIsAlly);
        when(guild.isEnemy(enemyGuild), config -> config.guild.commands.enemy.alreadyEnemy);
        when(guild.getEnemies().size() >= this.config.maxEnemiesBetweenGuilds, config -> config.guild.commands.enemy.enemiesLimit, formatter);

        if (enemyGuild.getEnemies().size() >= this.config.maxEnemiesBetweenGuilds) {
            this.messageService.getMessage(config -> config.guild.commands.enemy.targetEnemiesLimit)
                    .receiver(owner)
                    .with(formatter)
                    .send();
            return;
        }

        guild.addEnemy(enemyGuild);

        FunnyFormatter enemyFormatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag());

        FunnyFormatter enemyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.messageService.getMessage(config -> config.guild.commands.enemy.enemy)
                .receiver(owner)
                .with(enemyFormatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.enemy.enemyTarget)
                .receiver(enemyGuild.getOwner())
                .with(enemyIFormatter)
                .send();

        this.plugin.getIndividualNameTagManager().peek(manager -> {
            guild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
            enemyGuild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
        });
    }

}
