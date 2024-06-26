package net.dzikoysk.funnyguilds.feature.command.admin;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.command.CommandSender;
import panda.std.Option;
import panda.utilities.text.Joiner;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class ProtectionCommand extends AbstractFunnyCommand {

    private static final DateTimeFormatter PROTECTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @FunnyCommand(
            name = "${admin.protection.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        when(args.length < 3, config -> config.admin.commands.guild.protection.noValueGiven);
        String protectionDateAsString = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, 3)).toString();
        LocalDateTime protectionDate = Option.attempt(ParseException.class, () -> {
            return LocalDateTime.parse(protectionDateAsString, PROTECTION_DATE_FORMATTER);
        }).orThrow(() -> {
            return new InternalValidationException(config -> config.commands.validation.invalidDate);
        });

        guild.setProtection(protectionDate.atZone(ZoneId.systemDefault()).toInstant());

        this.messageService.getMessage(config -> config.admin.commands.guild.protection.changed)
                .receiver(sender)
                .with(guild)
                .with("{DATE}", protectionDateAsString)
                .send();
    }

}
