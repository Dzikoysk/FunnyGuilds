package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class TeleportCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.teleport.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when(!this.config.regionsEnabled, config -> config.guild.region.disabled);
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Region region = when(guild.getRegion(), config ->  config.admin.commands.validation.noRegion);

        player.teleport(region.getCenter());
        this.messageService.getMessage(config -> config.admin.commands.guild.teleport.teleported)
                .receiver(player)
                .with(guild)
                .send();
    }

}
