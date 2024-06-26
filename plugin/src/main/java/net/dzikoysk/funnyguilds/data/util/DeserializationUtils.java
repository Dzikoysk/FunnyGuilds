package net.dzikoysk.funnyguilds.data.util;

import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.BukkitUserProfile;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserProfile;
import org.bukkit.Location;
import panda.std.Option;

public final class DeserializationUtils {

    private DeserializationUtils() {
    }

    public static Option<User> deserializeUser(UserManager userManager, Object[] values) {
        UUID playerUniqueId = UUID.fromString((String) values[0]);
        String playerName = (String) values[1];

        UserProfile profile = new BukkitUserProfile(playerUniqueId, FunnyGuilds.getInstance().getFunnyServer()); // :(
        User user = userManager.create(playerUniqueId, playerName, profile);

        user.getRank().setPoints((int) values[2]);
        user.getRank().setKills((int) values[3]);
        user.getRank().setDeaths((int) values[4]);
        user.getRank().setAssists((int) values[5]);
        user.getRank().setLogouts((int) values[6]);

        Instant ban = (Instant) values[7];
        if (ban != null) {
            user.setBan(new UserBan((String) values[8], ban));
        }

        user.markUnchanged();
        return Option.of(user);
    }

    @SuppressWarnings("unchecked")
    public static Option<Guild> deserializeGuild(PluginConfiguration pluginConfiguration, GuildManager guildManager, Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize guild, caused by: null");
            return Option.none();
        }

        UUID guildUuid = (UUID) values[0];
        String guildName = (String) values[1];
        String rawGuildTag = (String) values[2];
        String guildTag = pluginConfiguration.guildTagKeepCase
                ? rawGuildTag
                : pluginConfiguration.guildTagUppercase
                ? rawGuildTag.toUpperCase(Locale.ROOT)
                : rawGuildTag.toLowerCase(Locale.ROOT);

        Guild guild = guildManager.findByUuid(guildUuid).orElseGet(() -> {
            Guild newGuild = new Guild(guildUuid, guildName, guildTag);
            guildManager.addGuild(newGuild);
            return newGuild;
        });

        guild.setOwner((User) values[3]);
        guild.setHome((Location) values[4]);

        if (FunnyGuilds.getInstance().getGuildConfiguration().isRegionsEnabled()) {
            guild.setRegion((Region) values[5]);
        }

        guild.setMembers((Set<User>) values[6]);
        guild.setAllies((Set<Guild>) values[7]);
        guild.setEnemies((Set<Guild>) values[8]);
        guild.setBorn((Instant) values[9]);
        guild.setValidity((Instant) values[10]);
        guild.setProtection((Instant) values[11]);
        guild.setLives((int) values[12]);
        guild.setBan((Instant) values[13]);
        guild.setDeputies((Set<User>) values[14]);
        guild.setPvP((boolean) values[15]);
        guild.deserializationUpdate();

        guild.markUnchanged();
        return Option.of(guild);
    }

    public static Option<Region> deserializeRegion(RegionManager regionManager, Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region, caused by: null");
            return Option.none();
        }

        String regionName = (String) values[0];
        Region region = regionManager.findByName(regionName).orElseGet(new Region(regionName, (Location) values[1]));

        // region.setSize((int) values[2]); // We don't set size here like before, now region size is calculated from region enlargement level (method below)
        regionManager.changeRegionEnlargement(region, (int) values[3]);
        region.update();

        region.markUnchanged();
        return Option.of(region);
    }

}
