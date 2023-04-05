package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.Validate;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class GuildManager {

    private final PluginConfiguration pluginConfiguration;
    private final Map<UUID, Guild> guildsMap = new ConcurrentHashMap<>();

    public GuildManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public int countGuilds() {
        return this.guildsMap.size();
    }

    /**
     * Gets the copied set of guilds.
     *
     * @return set of guild
     */
    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guildsMap.values());
    }

    /**
     * Deletes all loaded guilds data
     */
    public void clearGuilds() {
        this.guildsMap.clear();
    }

    /**
     * Gets the set of guilds from collection of strings (names).
     *
     * @param names collection of names
     * @return set of guild
     */
    public Set<Guild> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the set of guilds from collection of strings (tags).
     *
     * @param tags collection of tags
     * @return set of guild
     */
    public Set<Guild> findByTags(Collection<String> tags) {
        return PandaStream.of(tags)
                .flatMap(this::findByTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the guild.
     *
     * @param uuid the uuid of guild
     * @return the guild
     */
    public Option<Guild> findByUuid(UUID uuid) {
        return Option.of(this.guildsMap.get(uuid));
    }

    /**
     * Gets the guild.
     *
     * @param name       the name of guild
     * @param ignoreCase ignore the case of the name
     * @return the guild
     */
    public Option<Guild> findByName(String name, boolean ignoreCase) {
        if (ignoreCase) {
            return PandaStream.of(this.guildsMap.values()).find(guild -> guild.getName().equalsIgnoreCase(name));
        }

        return PandaStream.of(this.guildsMap.values()).find(guild -> guild.getName().equals(name));
    }

    /**
     * Gets the guild.
     *
     * @param name the name of guild
     * @return the guild
     */
    public Option<Guild> findByName(String name) {
        return this.findByName(name, false);
    }

    /**
     * Gets the guild.
     *
     * @param tag        the tag of guild
     * @param ignoreCase ignore the case of the tag
     * @return the guild
     */
    public Option<Guild> findByTag(String tag, boolean ignoreCase) {
        if (ignoreCase) {
            return PandaStream.of(this.guildsMap.values()).find(guild -> guild.getTag().equalsIgnoreCase(tag));
        }

        return PandaStream.of(this.guildsMap.values()).find(guild -> guild.getTag().equals(tag));
    }

    /**
     * Gets the guild.
     *
     * @param tag the tag of guild
     * @return the guild
     */
    public Option<Guild> findByTag(String tag) {
        return this.findByTag(tag, false);
    }

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(String name) {
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");

        Guild guild = new Guild(name);
        addGuild(guild);

        return guild;
    }
     */

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to guild
     * @param tag the tag which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(String name, String tag) {
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = create(name);
        guild.setTag(tag);

        return guild;
    }
    */

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param uuid the uuid which will be assigned to guild
     * @param name the name which will be assigned to guild
     * @param tag the tag which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(UUID uuid, String name, String tag) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = new Guild(uuid, name, tag);
        addGuild(guild);

        return guild;
    }
    */

    /**
     * Add guild to storage. If you think you should use this method you probably shouldn't.
     *
     * @param guild guild to add
     */
    public Guild addGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        this.guildsMap.put(guild.getUUID(), guild);
        return guild;
    }

    /**
     * Remove guild from storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#deleteGuild(FunnyGuilds, Guild)}.
     *
     * @param guild guild to remove
     */
    public void removeGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        this.guildsMap.remove(guild.getUUID());
    }

    /**
     * Delete guild in every possible way.
     *
     * @param guild guild to delete
     */
    public void deleteGuild(FunnyGuilds plugin, Guild guild) {
        if (guild == null) {
            return;
        }

        if (this.pluginConfiguration.regionsEnabled) {
            guild.getRegion()
                    .peek(region -> {
                        if (this.pluginConfiguration.heart.createEntityType != null) {
                            plugin.getGuildEntityHelper().despawnGuildEntity(guild);
                        }
                        else if (this.pluginConfiguration.heart.createMaterial != null &&
                                this.pluginConfiguration.heart.createMaterial != Material.AIR) {
                            Location center = region.getCenter().clone();

                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Block block = center.getBlock().getRelative(BlockFace.DOWN);

                                if (block.getLocation().getBlockY() > 1) {
                                    block.setType(Material.AIR);
                                }
                            });
                        }

                        plugin.getRegionManager().deleteRegion(plugin.getDataModel(), region);
                    });
        }

        guild.getMembers().forEach(User::removeGuild);
        guild.getAllies().forEach(ally -> ally.removeAlly(guild));
        this.getGuilds().forEach(globalGuild -> globalGuild.removeEnemy(guild));
        plugin.getIndividualNameTagManager().peek(manager -> {
            guild.getMembers().forEach(member -> plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
        });

        if (plugin.getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = ((FlatDataModel) plugin.getDataModel());
            dataModel.getGuildFile(guild).peek(FunnyIOUtils::deleteFile);
        }
        else if (plugin.getDataModel() instanceof SQLDataModel) {
            DatabaseGuildSerializer.delete(guild);
        }

        this.removeGuild(guild);
    }

    /**
     * Checks if guild with given name exists.
     *
     * @param name the guild name to check if exists
     * @return if guild with given name exists
     */
    public boolean nameExists(String name) {
        return this.findByName(name, true).isPresent();
    }

    /**
     * Checks if guild with given tag exists.
     *
     * @param tag the guild tag to check if exists
     * @return if guild with given tag exists
     */
    public boolean tagExists(String tag) {
        return this.findByTag(tag, true).isPresent();
    }

    /**
     * Spawn heart for guild. You probably shouldn't use this method.
     *
     * @param guild the guild for which heart should be spawned
     */
    public void spawnHeart(GuildEntityHelper guildEntityHelper, Guild guild) {
        if (this.pluginConfiguration.heart.createMaterial != null && this.pluginConfiguration.heart.createMaterial != Material.AIR) {
            guild.getRegion()
                    .flatMap(Region::getHeartBlock)
                    .peek(heart -> heart.setType(this.pluginConfiguration.heart.createMaterial));
            return;
        }

        guildEntityHelper.spawnGuildEntity(guild);
    }

}
