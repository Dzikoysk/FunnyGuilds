package net.dzikoysk.funnyguilds.feature.hooks.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent;
import net.dzikoysk.funnyguilds.feature.holograms.HologramsHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.config.HologramConfiguration;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class DecentHologramsHook extends HologramsHook implements Listener {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final Map<Guild, Hologram> holograms = new ConcurrentHashMap<>();

    public DecentHologramsHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public HookInitResult init() {
        Runnable updateTask = () -> this.plugin.getGuildManager().getGuilds().forEach(this::update);
        long updateInterval = this.config.heart.hologram.updateInterval;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, updateTask, 100L, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        return HookInitResult.SUCCESS;
    }

    @Override
    public void update(@NotNull Guild guild) {
        this.update(guild, false);
    }

    public void update(@NotNull Guild guild, boolean updateLocation) {
        HologramConfiguration holoConfig = this.config.heart.hologram;
        if (!holoConfig.enabled) {
            return;
        }

        Option<Location> guildCenterOption = guild.getCenter();
        if (guildCenterOption.isEmpty()) {
            return;
        }
        Location holoCenter = guildCenterOption.get()
                .add(holoConfig.locationCorrection);

        Hologram holo = this.holograms.computeIfAbsent(
                guild,
                (g) -> DHAPI.createHologram(prepareHologramName(guild), holoCenter, false)
        );

        if (updateLocation) {
            DHAPI.moveHologram(holo, holoCenter);
        }

        List<String> lines = new ArrayList<>();
        if (holoConfig.item != Material.AIR) {
            // A little hacky, but prevents from hologram blinking
            lines.add("#ICON:" + HologramItem.fromItemStack(new ItemStack(holoConfig.item)).getContent());
        }
        lines.addAll(PandaStream.of(holoConfig.displayedLines)
                .map(line -> this.plugin.getGuildPlaceholdersService().format(null, line, guild))
                .map(ChatUtils::colored)
                .toList());
        DHAPI.setHologramLines(holo, lines);
    }

    @Override
    public void configUpdated() {
        if (this.config.heart.hologram.enabled) {
            return;
        }

        this.holograms.values().forEach(Hologram::destroy);
        this.holograms.clear();
    }

    @EventHandler
    public void handleGuildDelete(GuildDeleteEvent event) {
        Hologram holo = this.holograms.remove(event.getGuild());
        if (holo == null) {
            return;
        }
        holo.destroy();
    }

    @EventHandler
    public void handleGuildCreate(GuildCreateEvent event) {
        this.update(event.getGuild());
    }

    @EventHandler
    public void handleGuildMove(GuildMoveEvent event) {
        this.update(event.getGuild(), true);
    }

    private static String prepareHologramName(Guild guild) {
        return "funnyguilds-guild-" + guild.getUUID();
    }

}
