package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public final class LocationUtils {

    private static final int LEGACY_MIN_HEIGHT = 0;

    private LocationUtils() {
    }

    @Contract(pure = true)
    public static Location toCenter(Location location) {
        Location loc = location.clone();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static double flatDistance(Location a, Location b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getZ() - a.getZ(), 2));
    }

    public static boolean checkWorld(Player player) {
        Set<String> blockedWorlds = FunnyGuilds.getInstance().getGuildConfiguration().create.blockedWorlds;
        return blockedWorlds != null && !blockedWorlds.isEmpty() && blockedWorlds.contains(player.getWorld().getName());
    }

    @Nullable
    public static Location parseLocation(String string) {
        if (string == null) {
            return null;
        }

        String[] split = string.split(",");
        if (split.length < 4) {
            return null;
        }

        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }

    public static boolean equals(Location location, Location to) {
        return (location.getBlockX() == to.getBlockX() && location.getBlockY() == to.getBlockY() && location.getBlockZ() == to.getBlockZ());
    }

    public static boolean equalsFlat(Location location, Location to) {
        return (location.getBlockX() == to.getBlockX() && location.getBlockZ() == to.getBlockZ());
    }

    public static String toString(Location location) {
        if (location == null) {
            return "";
        }

        World world = location.getWorld();
        if (world == null) {
            return "";
        }

        return location.getWorld().getName() + "," + (float) location.getX() + "," + (float) location.getY() +
                "," + (float) location.getZ();
    }

    public static String toString(Option<Location> location) {
        return toString(location.orNull());
    }

    public static int getMinHeight(World world) {
        try {
            return world.getMinHeight();
        }
        catch (NoSuchMethodError exception) {
            return LEGACY_MIN_HEIGHT;
        }
    }

}
