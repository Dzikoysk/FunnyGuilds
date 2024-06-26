package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

public abstract class WorldGuardHook extends AbstractPluginHook {

    protected StateFlag noPointsFlag;
    protected StateFlag noAssistsFlag;
    protected StateFlag noGuildsFlag;
    protected EnumFlag<FriendlyFireStatus> friendlyFireFlag;

    public WorldGuardHook(String name) {
        super(name);
    }

    protected void initFlags() {
        this.noPointsFlag = new StateFlag("fg-no-points", false);
        this.noAssistsFlag = new StateFlag("fg-no-assists", false);
        this.noGuildsFlag = new StateFlag("fg-no-guilds", false);
        this.friendlyFireFlag = new EnumFlag<FriendlyFireStatus>("fg-friendly-fire", FriendlyFireStatus.class) {
            @Override
            public FriendlyFireStatus getDefault() {
                return FriendlyFireStatus.INHERIT;
            }
        };
        this.registerFlags(this.noPointsFlag, this.noAssistsFlag, this.noGuildsFlag, this.friendlyFireFlag);
    }

    protected abstract void registerFlags(Flag<?>... flags);

    public abstract Option<ApplicableRegionSet> getRegionSet(Location location);

    public Set<ProtectedRegion> getRegions(Location location) {
        return this.getRegionSet(location).toStream()
                .flatMap(ApplicableRegionSet::getRegions)
                .filter(Objects::nonNull)
                .toSet();
    }

    public List<String> getRegionNames(Location location) {
        return PandaStream.of(this.getRegions(location))
                .map(ProtectedRegion::getId)
                .toList();
    }

    public boolean isInRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }
        return regionSet.get().size() != 0;
    }

    public boolean isInNonPointsRegion(Location location) {
        return PandaStream.of(this.getRegions(location))
                .find(region -> region.getFlag(this.noPointsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    public boolean isInNonAssistsRegion(Location location) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        return PandaStream.of(this.getRegions(location))
                .find(region -> region.getFlag(this.noAssistsFlag) == StateFlag.State.ALLOW
                        || config.assistsRegionsIgnored.contains(region.getId()))
                .isPresent();
    }

    public boolean isInNonGuildsRegion(Location location) {
        return PandaStream.of(this.getRegions(location))
                .find(region -> region.getFlag(this.noGuildsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    public FriendlyFireStatus getFriendlyFireStatus(Location location) {
        return PandaStream.of(this.getRegions(location))
                .map(region -> region.getFlag(this.friendlyFireFlag))
                .filter(Objects::nonNull)
                .filter(friendlyFireStatus -> friendlyFireStatus != FriendlyFireStatus.INHERIT)
                .head()
                .orElseGet(FriendlyFireStatus.INHERIT);
    }

    public enum FriendlyFireStatus {
        ALLOW,
        DENY,
        INHERIT
    }

}
