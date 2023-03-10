package net.dzikoysk.funnyguilds.shared.bukkit;

import java.text.DecimalFormat;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;

public final class NmsUtils {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");

    private NmsUtils() {
    }

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    public static String getFormattedTPS() {
        return FORMAT.format(getTpsInLastMinute());
    }

    public static double getTpsInLastMinute() {
        return Math.min(20.0D, Bukkit.getServer().getTPS()[0]);
    }

}
