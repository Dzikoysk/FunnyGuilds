package net.dzikoysk.funnyguilds.guild;

import javax.annotation.Nullable;
import panda.std.Option;

public final class RegionUtils {

    private RegionUtils() {
    }

    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

    public static String toString(Option<Region> region) {
        return toString(region.orNull());
    }

}
