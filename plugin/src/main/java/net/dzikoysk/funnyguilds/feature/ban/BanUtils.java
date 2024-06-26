package net.dzikoysk.funnyguilds.feature.ban;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import org.bukkit.ChatColor;
import panda.std.stream.PandaStream;

public final class BanUtils {

    private BanUtils() {
    }

    public static void ban(Guild guild, Duration time, String reason) {
        guild.setBan(Instant.now().plus(time));
        PandaStream.of(guild.getMembers())
                .map(member -> ban(member, time, reason))
                .forEach(member -> member.getProfile().kick(getBanMessage(member)));
    }

    public static User ban(User user, Duration time, String reason) {
        user.setBan(new UserBan(reason, Instant.now().plus(time)));
        return user;
    }

    public static void unban(Guild guild) {
        guild.getMembers().forEach(BanUtils::unban);
    }

    public static void unban(User user) {
        user.setBan(null);
    }

    public static void checkIfBanShouldExpire(User user) {
        user.getBan()
                .filterNot(UserBan::isBanned)
                .peek(time -> user.setBan(null));
    }

    public static String getBanMessage(User user) {
        return user.getBan()
                .map(ban -> {
                    FunnyGuilds plugin = FunnyGuilds.getInstance();
                    ZoneId timeZone = plugin.getPluginConfiguration().timeZone;
                    MessageService messageService = plugin.getMessageService();

                    FunnyFormatter formatter = new FunnyFormatter()
                            .register("{NEWLINE}", ChatColor.RESET + "\n")
                            .register("{DATE}", messageService.get(user, config -> config.dateFormat).format(ban.getTime(), timeZone))
                            .register("{REASON}", ban.getReason())
                            .register("{PLAYER}", user.getName());
                    return messageService.get(user, config -> config.admin.commands.guild.ban.bannedKick, formatter);
                })
                .orElseGet("");
    }

}
