package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public final class ValidityUtils {

    private ValidityUtils() {
    }

    public static void broadcast(@Nullable Guild guild) {
        if (guild == null) {
            return;
        }

        MessageService messageService = FunnyGuilds.getInstance().getMessageService();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{GUILD}", guild.getName());

        Option<Region> regionOption = guild.getRegion();
        boolean hasRegion = regionOption.isPresent();

        messageService.getMessage(config -> config.guild.commands.validity.expiredBroadcast)
                .broadcast()
                .with(formatter)
                .with(CommandSender.class, receiver -> {
                    FunnyFormatter cordFormatter = new FunnyFormatter();
                    if (hasRegion) {
                        Location center = regionOption.get().getCenter();
                        cordFormatter.register("{X}", center.getBlockX());
                        cordFormatter.register("{Y}", center.getBlockY());
                        cordFormatter.register("{Z}", center.getBlockZ());
                    }
                    else {
                        String noInformation = messageService.get(receiver, config -> config.guild.commands.validity.noCoordinates);
                        cordFormatter.register("{X}", noInformation);
                        cordFormatter.register("{Y}", noInformation);
                        cordFormatter.register("{Z}", noInformation);
                    }
                    return cordFormatter;
                })
                .send();
    }

}
