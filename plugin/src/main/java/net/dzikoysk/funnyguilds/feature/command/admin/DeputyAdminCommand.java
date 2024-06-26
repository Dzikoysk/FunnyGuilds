package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeputyAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.deputy.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3 online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(args.length < 2, config -> config.commands.validation.noNickGiven);

        User deputyUser = UserValidation.requireUserByName(args[1]);
        when(!guild.isMember(deputyUser), config -> config.admin.commands.validation.notMemberOf);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(AdminUtils.getCause(admin), admin, guild, deputyUser))) {
            return;
        }

        FunnyFormatter formatter = FunnyFormatter.of("{PLAYER}", deputyUser.getName());

        if (deputyUser.isDeputy()) {
            guild.removeDeputy(deputyUser);
            this.messageService.getMessage(config -> config.guild.commands.deputy.removed)
                    .receiver(sender)
                    .send();
            this.messageService.getMessage(config -> config.guild.commands.deputy.removedTarget)
                    .receiver(deputyUser)
                    .send();
            this.messageService.getMessage(config -> config.guild.commands.deputy.removedMembers)
                    .receiver(guild)
                    .send();
            return;
        }

        guild.addDeputy(deputyUser);
        this.messageService.getMessage(config -> config.guild.commands.deputy.set)
                .receiver(sender)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.deputy.setTarget)
                .receiver(deputyUser)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.deputy.setMembers)
                .receiver(guild)
                .send();
    }

}
