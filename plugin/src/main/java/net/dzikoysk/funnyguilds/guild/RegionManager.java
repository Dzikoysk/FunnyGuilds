package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.Validate;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class RegionManager {

    private final PluginConfiguration pluginConfiguration;
    private final Map<Long, Set<Region>> regions = new ConcurrentHashMap<>();

    public RegionManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public int countRegions() {
        return this.getRegions().size();
    }

    /**
     * Gets the copied set of regions.
     *
     * @return set of regions
     */
    public Set<Region> getRegions() {
        return this.regions.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Deletes all loaded regions data
     */
    public void clearRegions() {
        this.regions.clear();
    }

    /**
     * Gets the region.
     *
     * @param name       the name of region (probably name of guild)
     * @param ignoreCase ignore the case of the tag
     * @return the guild
     */
    public Option<Region> findByName(String name, boolean ignoreCase) {
        return PandaStream.of(this.regions.values())
                .flatMap(r -> r)
                .find(region -> ignoreCase ? region.getName().equalsIgnoreCase(name) : region.getName().equals(name));
    }

    /**
     * Gets the region.
     *
     * @param name the name of region (probably name of guild)
     * @return the region
     */
    public Option<Region> findByName(String name) {
        return this.findByName(name, false);
    }

    /**
     * Gets the region.
     *
     * @param location the location of region
     * @return the region
     */
    public Option<Region> findRegionAtLocation(Location location) {
        long packedPos = packChunkPosition(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        Set<Region> applicableRegions = this.regions.get(packedPos);

        if (applicableRegions == null || applicableRegions.isEmpty()) {
            return Option.none();
        }

        return PandaStream.of(applicableRegions).find(region -> region.isIn(location));
    }

    /**
     * Checks if there is a region in the given location.
     *
     * @param location the location of region
     * @return if given location is in region
     */
    public boolean isInRegion(Location location) {
        return this.findRegionAtLocation(location).isPresent();
    }

    public boolean isAnyPlayerInRegion(Region region, Collection<UUID> ignoredUuids) {
        if (!this.pluginConfiguration.regionsEnabled) {
            return false;
        }

        FunnyBox box = region.toBox();
        return PandaStream.of(Bukkit.getOnlinePlayers())
                .filter(player -> !ignoredUuids.contains(player.getUniqueId()))
                .find(player -> box.contains(player.getLocation()))
                .isPresent();
    }

    public boolean isAnyUserInRegion(Region region, Collection<User> ignoredUsers) {
        return this.isAnyPlayerInRegion(region, PandaStream.of(ignoredUsers)
                .map(User::getUUID)
                .collect(Collectors.toSet()));
    }

    /**
     * Checks if there is any region in area (used to check if it's possible to create a new guild in given location).
     *
     * @param center the center of region
     * @return if is region nearby
     */
    public boolean isNearRegion(Location center) {
        if (center == null) {
            return false;
        }

        int size = this.pluginConfiguration.regionSize + (this.pluginConfiguration.enlargeItems.size() * this.pluginConfiguration.enlargeSize);
        int requiredDistance = (2 * size) + this.pluginConfiguration.regionMinDistance;

        return PandaStream.of(this.regions.values())
                .flatMap(r -> r)
                .map(Region::getCenter)
                .filterNot(regionCenter -> regionCenter.equals(center))
                .filter(regionCenter -> regionCenter.getWorld().equals(center.getWorld()))
                .find(regionCenter -> LocationUtils.flatDistance(regionCenter, center) < requiredDistance)
                .isPresent();
    }

    /**
     * Checks if given block is guild's heart.
     *
     * @param block block to check
     * @return if given block is guild's heart
     */
    public boolean isGuildHeart(Block block) {
        Material heartMaterial = this.pluginConfiguration.heart.createMaterial;
        if (heartMaterial == null || block.getType() != heartMaterial) {
            return false;
        }

        Location blockLocation = block.getLocation();
        return this.findRegionAtLocation(blockLocation)
                .map(region -> region.getHeart().contentEquals(blockLocation))
                .orElseGet(false);
    }

    /**
     * Add region to storage.
     * If you think you should use this method you probably shouldn't.
     *
     * @param region region to add
     */
    public void addRegion(Region region) {
        Validate.notNull(region, "region can't be null!");

        this.forEachChunkPositionInRegion(region, (chunkX, chunkZ) -> {
            long packedPos = packChunkPosition(chunkX, chunkZ);

            Set<Region> regionsAtChunk = this.regions.computeIfAbsent(packedPos, k -> new HashSet<>());
            regionsAtChunk.add(region);
        });
    }

    /**
     * Remove region from storage.
     * If you think you should use this method you probably shouldn't - instead use {@link RegionManager#deleteRegion(DataModel, Region)}.
     *
     * @param region region to remove
     */
    public void removeRegion(Region region) {
        Validate.notNull(region, "region can't be null!");
        
        if (!this.regionExists(region.getName())) {
            return;
        }

        this.forEachChunkPositionInRegion(region, (chunkX, chunkZ) -> {
            long packedPos = packChunkPosition(chunkX, chunkZ);

            this.regions.computeIfPresent(packedPos, (key, set) -> {
                set.remove(region);
                return set;
            });
        });
    }

    public void moveRegionCenter(Region region, Location center) {
        Validate.notNull(region, "region can't be null!");
        Validate.notNull(center, "center can't be null!");

        this.removeRegion(region);
        region.setCenter(center);
        this.addRegion(region);
    }

    public void changeRegionEnlargement(Region region, int level) {
        Validate.notNull(region, "region can't be null!");
        Validate.isTrue(level >= 0, "level can't be negative!");

        int maxEnlargeLevel = this.pluginConfiguration.enlargeItems.size();
        if (level > maxEnlargeLevel) {
            level = maxEnlargeLevel;
        }

        this.removeRegion(region);
        region.setEnlargementLevel(level);
        region.setSize(this.pluginConfiguration.regionSize + (level * this.pluginConfiguration.enlargeSize));
        this.addRegion(region);
    }

    /**
     * Delete region from storage and from database.
     *
     * @param region region to delete
     * @deprecated for removal in the future with database rework (GH-1402)
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public void deleteRegion(DataModel dataModel, Region region) {
        Validate.notNull(region, "region can't be null!");

        if (dataModel instanceof FlatDataModel) {
            ((FlatDataModel) dataModel).getRegionFile(region).peek(FunnyIOUtils::deleteFile);
        }

        if (dataModel instanceof SQLDataModel) {
            DatabaseRegionSerializer.delete(region);
        }

        this.removeRegion(region);
    }

    /**
     * Checks if region with given name exists.
     *
     * @param name region name
     * @return if region exists
     */
    public boolean regionExists(String name) {
        return this.findByName(name).isPresent();
    }

    /**
     * Calculates chunks that are in the bounds of the given region
     * and applies given function for every chunk position.
     */
    private void forEachChunkPositionInRegion(Region region, BiConsumer<Integer, Integer> chunkPosFunc) {
        int firstX = region.getFirstCorner().getBlockX() >> 4;
        int firstZ = region.getFirstCorner().getBlockZ() >> 4;

        int secondX = region.getSecondCorner().getBlockX() >> 4;
        int secondZ = region.getSecondCorner().getBlockZ() >> 4;

        int startX = Math.min(firstX, secondX);
        int startZ = Math.min(firstZ, secondZ);

        int endX = Math.max(firstX, secondX);
        int endZ = Math.max(firstZ, secondZ);

        for (int chunkX = startX; chunkX <= endX; chunkX++) {
            for (int chunkZ = startZ; chunkZ <= endZ; chunkZ++) {
                chunkPosFunc.accept(chunkX, chunkZ);
            }
        }
    }

    private static long packChunkPosition(int chunkX, int chunkZ) {
        return (long) chunkX & 0xFFFFFFFFL | ((long) chunkZ & 0xFFFFFFFFL) << 32;
    }
}
