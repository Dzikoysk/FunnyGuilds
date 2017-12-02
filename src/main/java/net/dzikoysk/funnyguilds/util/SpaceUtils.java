package net.dzikoysk.funnyguilds.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class SpaceUtils {

    private SpaceUtils() {
    }

    public static List<Location> sphere(Location loc, int radius, int height, boolean hollow, boolean sphere, int plusY) {

        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        for (int x = cx - radius; x <= (cx + radius); x++) {
            for (int z = cz - radius; z <= (cz + radius); z++) {
                for (int y = (sphere ? (cy - radius) : cy); y < (sphere ? (cy + radius) : (cy + height)); y++) {
                    double dist = ((cx - x) * (cx - x)) + ((cz - z) * (cz - z)) + (sphere ? ((cy - y) * (cy - y)) : 0);

                    if ((dist < (radius * radius)) && !(hollow && (dist < ((radius - 1) * (radius - 1))))) {
                        Location l = new Location(loc.getWorld(), x, y + plusY, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }
}
