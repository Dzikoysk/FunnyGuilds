package net.dzikoysk.funnyguilds.feature.command.admin;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Duration;
import java.time.Instant;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class ValidityAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.validity.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(guild.isBanned(), config -> config.admin.commands.guild.validity.banned);

        when(args.length < 2, config -> config.admin.commands.guild.validity.noValueGiven);
        Duration time = TimeUtils.parseTime(args[1]);
        when(time.toMillis() < 1, config -> config.commands.validation.invalidTime);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(AdminUtils.getCause(admin), admin, guild, time))) {
            return;
        }

        Instant validity = guild.getValidity();
        if (validity.toEpochMilli() == 0) {
            validity = Instant.now();
        }

        validity = validity.plus(time);
        guild.setValidity(validity);

        Instant finalValidity = validity;
        this.messageService.getMessage(config -> config.admin.commands.guild.validity.changed)
                .receiver(sender)
                .with(guild)
                .with(CommandSender.class, receiver -> Replacement.of(
                        "{VALIDITY}",
                        this.messageService.get(receiver, config -> config.dateFormat).format(finalValidity, this.config.timeZone)
                ))
                .send();
    }

}
