package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import panda.std.Option;

public final class FlatGuildSerializer {

    private FlatGuildSerializer() {
    }

    public static Option<Guild> deserialize(File file) {
        if (file.isDirectory()) {
            return Option.none();
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();
        UserManager userManager = plugin.getUserManager();
        GuildManager guildManager = plugin.getGuildManager();
        RegionManager regionManager = plugin.getRegionManager();
        PluginConfiguration config = plugin.getPluginConfiguration();

        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        String tag = wrapper.getString("tag");
        String ownerName = wrapper.getString("owner");
        String deputyName = wrapper.getString("deputy");
        String hs = wrapper.getString("home");
        String regionName = wrapper.getString("region");
        boolean pvp = wrapper.getBoolean("pvp");
        Instant born = TimeUtils.positiveOrNullInstant(wrapper.getLong("born"));
        Instant validity = TimeUtils.positiveOrNullInstant(wrapper.getLong("validity"));
        Instant protection = TimeUtils.positiveOrNullInstant(wrapper.getLong("protection"));
        Instant ban = TimeUtils.positiveOrNullInstant(wrapper.getLong("ban"));
        int lives = wrapper.getInt("lives");

        if (name == null) {
            logger.deserialize("Cannot deserialize guild, caused by: name is null");
            return Option.none();
        }

        if (tag == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: tag is null");
            return Option.none();
        }

        if (ownerName == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: owner is null");
            return Option.none();
        }

        Region region = null;
        if (config.regionsEnabled) {
            if (regionName == null) {
                logger.deserialize("Cannot deserialize guild: " + name + ", caused by: region is null");
                return Option.none();
            }

            Option<Region> regionOption = regionManager.findByName(regionName);
            if (regionOption.isEmpty()) {
                logger.deserialize("Cannot deserialize guild: " + name + ", caused by: region (object) is null");
                return Option.none();
            }

            region = regionOption.get();
        }

        UUID uuid = UUID.randomUUID();
        if (id != null && !id.isEmpty()) {
            uuid = UUID.fromString(id);
        }

        Option<User> ownerOption = userManager.findByName(ownerName);
        if (ownerOption.isEmpty()) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: owner is null");
            return Option.none();
        }

        Set<User> deputies = ConcurrentHashMap.newKeySet(1);
        if (!FunnyStringUtils.isEmpty(deputyName)) {
            deputies = userManager.findByNames(FunnyStringUtils.fromString(deputyName));
        }

        Location home = null;
        if (region != null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        Set<String> memberNames = loadSet(wrapper, "members");
        if (memberNames == null || memberNames.isEmpty()) {
            memberNames = new HashSet<>();
            memberNames.add(ownerName);
        }

        Set<User> members = userManager.findByNames(memberNames);
        Set<Guild> allies = guildManager.findByNames(loadSet(wrapper, "allies"));
        Set<Guild> enemies = guildManager.findByNames(loadSet(wrapper, "enemies"));

        if (born == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: born is null");
            return Option.none();
        }

        if (validity == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: validity is null");
            return Option.none();
        }

        if (protection == null) {
            logger.deserialize("Cannot deserialize guild: " + name + ", caused by: protection is null");
            return Option.none();
        }

        if (lives == 0) {
            lives = config.warLives;
        }

        Object[] values = new Object[17];
        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = ownerOption.get();
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = allies;
        values[8] = enemies;
        values[9] = born;
        values[10] = validity;
        values[11] = protection;
        values[12] = lives;
        values[13] = ban;
        values[14] = deputies;
        values[15] = pvp;

        return DeserializationUtils.deserializeGuild(config, guildManager, values);
    }

    public static boolean serialize(Guild guild) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        if (guild.getOwner() == null) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + ", caused by: owner is null");
            return false;
        }

        if (!guild.hasRegion() && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + ", caused by: region is null");
            return false;
        }

        Option<File> fileOption = dataModel.getGuildFile(guild);
        if (fileOption.isEmpty()) {
            return false;
        }

        File guildFile = fileOption.get();
        if (guildFile.isDirectory()) {
            return false;
        }

        YamlWrapper wrapper = new YamlWrapper(guildFile);
        wrapper.set("uuid", guild.getUUID().toString());
        wrapper.set("name", guild.getName());
        wrapper.set("tag", guild.getTag());
        wrapper.set("owner", guild.getOwner().getName());
        wrapper.set("home", LocationUtils.toString(guild.getHome()));
        wrapper.set("members", new ArrayList<>(Entity.names(guild.getMembers())));
        wrapper.set("region", RegionUtils.toString(guild.getRegion()));
        wrapper.set("regions", null);
        wrapper.set("allies", new ArrayList<>(Entity.names(guild.getAllies())));
        wrapper.set("enemies", new ArrayList<>(Entity.names(guild.getEnemies())));
        wrapper.set("born", guild.getBorn().toEpochMilli());
        wrapper.set("validity", guild.getValidity().toEpochMilli());
        wrapper.set("protection", guild.getProtection().toEpochMilli());
        wrapper.set("lives", guild.getLives());
        wrapper.set("ban", guild.getBan().map(Instant::toEpochMilli).orElseGet(0L));
        wrapper.set("pvp", guild.hasPvPEnabled());
        wrapper.set("deputy", FunnyStringUtils.join(Entity.names(guild.getDeputies()), false));

        wrapper.save();
        guild.markUnchanged();

        return true;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> loadSet(YamlWrapper data, String key) {
        Object collection = data.get(key);

        if (collection instanceof List) {
            return new HashSet<>((List<String>) collection);
        }
        else if (collection instanceof Set) {
            return (Set<String>) collection;
        }
        else if (collection instanceof ConfigurationSection) {
            return ((ConfigurationSection) collection).getKeys(false);
        }

        return Collections.emptySet();
    }

}
