package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.LocalTime;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class TntCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.tnt.name}",
            description = "${user.tnt.description}",
            aliases = "${user.tnt.aliases}",
            permission = "funnyguilds.tnt",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        when(!this.config.tntProtection.time.enabled, config -> config.commands.tnt.infoAlways);

        LocalTime now = LocalTime.now();
        LocalTime start = this.config.tntProtection.time.startTime.getTime();
        LocalTime end = this.config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = this.config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PROTECTION_START}", this.config.tntProtection.time.startTime.getFormattedTime())
                .register("{PROTECTION_END}", this.config.tntProtection.time.endTime.getFormattedTime());

        this.messageService.getMessage(config -> config.commands.tnt.info)
                .receiver(sender)
                .with(formatter)
                .send();
        this.messageService
                .getMessage(isWithinTimeframe
                        ? config -> config.commands.tnt.disabled
                        : config -> config.commands.tnt.enabled
                )
                .receiver(sender)
                .send();
    }

}
