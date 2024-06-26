package net.dzikoysk.funnyguilds.feature.protection;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Triple;

public final class ProtectionSystem {

    private ProtectionSystem() {
    }

    public static Option<Triple<Player, Guild, ProtectionType>> isProtected(Player player, Location location, boolean includeBuildLock) {
        if (player == null || location == null) {
            return Option.none();
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();

        Option<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return Option.none();
        }
        Region region = regionOption.get();
        Guild guild = region.getGuild();

        PluginConfiguration config = plugin.getPluginConfiguration();
        HeartConfiguration heartConfig = config.heart;
        if (region.getHeart().contentEquals(location)) {
            Material heartMaterial = heartConfig.createMaterial;
            return Option.when(heartMaterial != null && heartMaterial != Material.AIR, Triple.of(player, guild, ProtectionType.HEART));
        }

        if (player.hasPermission("funnyguilds.admin.build")) {
            return Option.none();
        }

        Option<User> userOption = plugin.getUserManager().findByUuid(player.getUniqueId());
        if (!userOption.is(guild::isMember)) {
            return Option.of(Triple.of(player, guild, ProtectionType.UNAUTHORIZED));
        }

        if (includeBuildLock && !guild.canBuild()) {
            return Option.of(Triple.of(player, guild, ProtectionType.LOCKED));
        }

        if (heartConfig.interactionProtection.enabled && isGuildHeartProtectedRegion(location)) {
            return Option.of(Triple.of(player, guild, ProtectionType.HEART_INTERACTION));
        }

        return Option.none();
    }

    public static boolean isGuildHeartProtectedRegion(Location location) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        Option<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return false;
        }
        Region region = regionOption.get();
        Guild guild = region.getGuild();

        HeartConfiguration heartConfig = plugin.getPluginConfiguration().heart;
        return guild.getEnderCrystal()
                .map(Location::getBlock)
                .map(FunnyBox::of)
                .map(box -> box.expandDirectional(heartConfig.interactionProtection.firstCorner, heartConfig.interactionProtection.secondCorner))
                .map(box -> box.contains(location))
                .orElseGet(false);
    }

    public static void defaultResponse(Triple<Player, Guild, ProtectionType> result) {
        Player player = result.getFirst();
        ProtectionType protectionType = result.getThird();

        Function<MessageConfiguration, Sendable> messageSupplier;
        switch (protectionType) {
            case UNAUTHORIZED:
                messageSupplier = config -> config.guild.region.protection.unauthorized;
                break;
            case HEART:
                messageSupplier = config -> config.guild.region.protection.center;
                break;
            case HEART_INTERACTION:
                messageSupplier = config -> config.guild.region.protection.heart;
                break;
            case LOCKED:
                ProtectionSystem.sendRegionExplodeMessage(player, result.getSecond());
                return;
            default:
                messageSupplier = config -> config.guild.region.protection.other;
                break;
        }
        FunnyGuilds.getInstance().getMessageService().getMessage(messageSupplier)
                .receiver(player)
                .send();
    }

    private static void sendRegionExplodeMessage(Player player, Guild guild) {
        guild.getBuild().peek(build -> {
            Duration time = Duration.between(Instant.now(), build);
            FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.guild.region.explosion.interaction)
                    .with(FunnyFormatter.of("{TIME}", time.getSeconds()))
                    .receiver(player)
                    .send();
        });
    }

    public enum ProtectionType {

        UNAUTHORIZED,
        LOCKED,
        HEART,
        HEART_INTERACTION

    }

}
