package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent;
import net.dzikoysk.funnyguilds.feature.ban.BanUtils;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class UnbanCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.unban.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(!guild.isBanned(), config -> config.admin.commands.guild.unban.notBanned);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildUnbanEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }

        BanUtils.unban(guild);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{NAME}", guild.getName())
                .register("{TAG}", guild.getName())
                .register("{ADMIN}", sender.getName());

        this.messageService.getMessage(config -> config.admin.commands.guild.unban.unbanned)
                .receiver(sender)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.admin.commands.guild.unban.unbannedBroadcast)
                .broadcast()
                .with(formatter)
                .send();
    }

}
