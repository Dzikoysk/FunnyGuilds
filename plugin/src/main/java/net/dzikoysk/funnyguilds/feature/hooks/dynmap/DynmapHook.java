package net.dzikoysk.funnyguilds.feature.hooks.dynmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public class DynmapHook extends AbstractPluginHook implements Listener {

    private final FunnyGuilds plugin;
    private final PluginConfiguration.DynmapHook hookConfig;
    private final GuildManager guildManager;

    private DynmapCommonAPI dynmapApi;
    private MarkerAPI markerApi;
    private MarkerSet guildsMarkerSet;
    private PlayerSet playersMarkerSet;

    private final Map<Guild, DynmapGuild> guilds = new ConcurrentHashMap<>();

    public DynmapHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
        this.hookConfig = plugin.getPluginConfiguration().dynmapHook;
        this.guildManager = plugin.getGuildManager();
    }

    @Override
    public HookInitResult init() {
        this.dynmapApi = (DynmapCommonAPI) FunnyGuilds.getInstance().getServer().getPluginManager().getPlugin("dynmap");
        this.markerApi = this.dynmapApi.getMarkerAPI();

        this.guildsMarkerSet = this.markerApi.createMarkerSet("fg_guilds", this.hookConfig.guildSetLabel, null, false);
        this.guildManager.getGuilds().forEach(this::getOrCreateDynmapGuild);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> this.guilds.forEach((guild, dynmapGuild) -> {
            dynmapGuild.getCenterMarker().peek(marker -> marker.setLabel(this.plugin.getGuildPlaceholdersService().format(null, this.hookConfig.center.label, guild), true));
            dynmapGuild.getAreaMarker().peek(marker -> marker.setLabel(this.plugin.getGuildPlaceholdersService().format(null, this.hookConfig.area.label, guild), true));
        }), 0, this.hookConfig.updateInterval);

        return HookInitResult.SUCCESS;
    }

    @EventHandler(ignoreCancelled = true)
    public void handleGuildDelete(GuildDeleteEvent event) {
        this.deleteDynmapGuild(event.getGuild());
    }

    @EventHandler(ignoreCancelled = true)
    public void handleGuildCreate(GuildCreateEvent event) {
        this.forceUpdateDynmapGuild(event.getGuild());
    }

    @EventHandler(ignoreCancelled = true)
    public void handleGuildMove(GuildMoveEvent event) {
        this.forceUpdateDynmapGuild(event.getGuild());
    }

    @EventHandler(ignoreCancelled = true)
    public void handleGuildEnlarge(GuildEnlargeEvent event) {
        this.forceUpdateDynmapGuild(event.getGuild());
    }

    private @NotNull Option<DynmapGuild> getDynmapGuild(@NotNull Guild guild) {
        return Option.of(this.guilds.get(guild));
    }

    private @NotNull Option<DynmapGuild> getOrCreateDynmapGuild(@NotNull Guild guild) {
        return Option.of(this.guilds.computeIfAbsent(guild, key -> {
            Option<Region> regionOption = guild.getRegion();
            if (regionOption.isEmpty()) {
                return null;
            }
            Region region = regionOption.get();
            World world = region.getWorld();

            Option<Marker> centerMarker = Option.when(this.hookConfig.center.enabled, () -> {
                Location center = LocationUtils.toCenter(region.getCenter());
                if (this.hookConfig.center.hideCenterY) {
                    center.setY(world.getHighestBlockYAt(center) + 0.5);
                }

                return this.guildsMarkerSet.createMarker(
                        "fg_guild_center_" + guild.getName(),
                        this.plugin.getGuildPlaceholdersService().format(null, this.hookConfig.center.label, guild),
                        region.getWorld().getName(),
                        center.getX(),
                        center.getY(),
                        center.getZ(),
                        this.markerApi.getMarkerIcon(this.hookConfig.center.icon),
                        false
                );
            });

            Option<AreaMarker> areaMarker = Option.when(this.hookConfig.area.enabled, () -> {
                Location firstCorner = region.getFirstCorner();
                Location secondCorner = region.getSecondCorner();

                AreaMarker marker = this.guildsMarkerSet.createAreaMarker(
                        "fg_guild_area_" + guild.getName(),
                        this.plugin.getGuildPlaceholdersService().format(null, this.hookConfig.area.label, guild),
                        true,
                        region.getWorld().getName(),
                        new double[]{firstCorner.getX(), secondCorner.getX()},
                        new double[]{firstCorner.getZ(), secondCorner.getZ()},
                        false
                );

                PluginConfiguration.DynmapHook.Area.Fill fillStyle = this.hookConfig.area.fill;
                marker.setFillStyle(fillStyle.opacity, fillStyle.color.asRGB());

                PluginConfiguration.DynmapHook.Area.Line lineStyle = this.hookConfig.area.line;
                marker.setLineStyle(lineStyle.weight, lineStyle.opacity, lineStyle.color.asRGB());

                return marker;
            });

            return new DynmapGuild(centerMarker, areaMarker);
        }));
    }

    private void deleteDynmapGuild(@NotNull Guild guild) {
        DynmapGuild remove = this.guilds.remove(guild);
        if (remove != null) {
            remove.delete();
        }
    }

    private void forceUpdateDynmapGuild(@NotNull Guild guild) {
        this.deleteDynmapGuild(guild);
        this.getOrCreateDynmapGuild(guild);
    }

    private static class DynmapGuild {

        private final Option<Marker> centerMarker;
        private final Option<AreaMarker> areaMarker;

        private DynmapGuild(Option<Marker> centerMarker, Option<AreaMarker> areaMarker) {
            this.centerMarker = centerMarker;
            this.areaMarker = areaMarker;
        }

        public Option<Marker> getCenterMarker() {
            return this.centerMarker;
        }

        public Option<AreaMarker> getAreaMarker() {
            return this.areaMarker;
        }

        private void delete() {
            this.centerMarker.peek(Marker::deleteMarker);
            this.areaMarker.peek(AreaMarker::deleteMarker);
        }

    }

}
