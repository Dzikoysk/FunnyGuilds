package net.dzikoysk.funnyguilds.feature.security;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.security.cheat.SecurityFreeCam;
import net.dzikoysk.funnyguilds.feature.security.cheat.SecurityReach;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import panda.std.Option;

public final class SecuritySystem {

    private static final double RAYCAST_STEP_SIZE = 0.1D;
    private static final double ADDITIONAL_SNEAKING_HEIGHT_CURSOR = 0.35;
    private static final Cache<User, Integer> PLAYERS_VIOLATION_LEVEL = CacheBuilder.newBuilder()
            .ticker(Ticker.systemTicker())
            .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
            .build();

    private SecuritySystem() {
    }

    public static boolean onHitCrystal(Player player, Guild guild) {
        scan(player, guild);
        return SecurityUtils.isBlocked(FunnyGuilds.getInstance().getUserManager().findByPlayer(player).orNull());
    }

    private static void scan(Player player, Guild guild) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        if (!config.regionsEnabled) {
            return;
        }

        if (!config.securitySystem.enabled) {
            return;
        }

        Option<Region> regionOption = guild.getRegion();
        if (regionOption.isEmpty()) {
            return;
        }
        Region region = regionOption.get();
        Location center = region.getCenter();

        double x = center.getX() + 0.5;
        double y = center.getY();
        double z = center.getZ() + 0.5;

        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        Vector origin = player.isSneaking()
                ? eye.add(0.0, ADDITIONAL_SNEAKING_HEIGHT_CURSOR, 0.0).toVector()
                : eye.toVector();

        FunnyBox funnyBox = config.heart.createEntityType != null
                ? new FunnyBox(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0)
                : FunnyBox.of(player.getWorld().getBlockAt(center));

        Vector hitPoint = funnyBox.rayTrace(origin, direction, RAYCAST_STEP_SIZE, 6);
        if (hitPoint == null) {
            hitPoint = center.toVector();
        }
        double distance = hitPoint.distance(origin);

        SecurityFreeCam.on(player, guild, origin, hitPoint, distance);
        SecurityReach.on(player, distance);
    }

    static Cache<User, Integer> getPlayersViolationLevel() {
        return PLAYERS_VIOLATION_LEVEL;
    }

}
