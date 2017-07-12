package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {

    public static List<Region> regions = new ArrayList<>();

    public static List<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    public static Region get(String name) {
        if (name == null) {
            return null;
        }
        for (Region region : regions) {
            if (region != null && region.getName() != null && region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }
        return null;
    }

    public static boolean isIn(Location loc) {
        for (Region region : regions) {
            if (region.isIn(loc)) {
                return true;
            }
        }
        return false;
    }

    public static Region getAt(Location loc) {
        for (Region region : regions) {
            if (region.isIn(loc)) {
                return region;
            }
        }
        return null;
    }

    public static boolean isNear(Location center) {
        if (center == null) {
            return false;
        }
        for (Region region : regions) {
            if (region.getCenter() == null) {
                return false;
            }
            if (!center.getWorld().equals(region.getCenter().getWorld())) {
                return false;
            }
            double distance = center.distance(region.getCenter());
            PluginConfig s = Settings.getConfig();
            int i = s.regionSize;
            if (s.enlargeItems != null) {
                i += s.enlargeItems.size() * s.enlargeSize;
            }
            if (distance < (2 * i + s.regionMinDistance)) return true;
        }
        return false;
    }

    public static void delete(Region region) {
        if (Settings.getConfig().dataType.flat) {
            Flat.getRegionFile(region).delete();
        }
        if (Settings.getConfig().dataType.mysql) {
            new DatabaseRegion(region).delete();
        }
        region.delete();
    }

    public static List<String> getNames(List<Region> lsg) {
        List<String> list = new ArrayList<>();
        if (lsg == null) {
            return list;
        }
        for (Region r : lsg) {
            if (r != null && r.getName() != null) {
                list.add(r.getName());
            }
        }
        return list;
    }

    public static void addRegion(Region region) {
        regions.add(region);
    }

    public static void removeRegion(Region region) {
        regions.remove(region);
    }
}
