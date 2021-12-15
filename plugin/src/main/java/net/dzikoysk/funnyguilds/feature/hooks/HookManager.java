package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.EmptyHologramManagerImpl;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.FunnyHologramManager;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.HolographicDisplaysHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEditHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;

public class HookManager {

    public static WorldGuardHook WORLD_GUARD;
    public static WorldEditHook WORLD_EDIT;
    public static FunnyTabHook FUNNY_TAB;
    public static VaultHook VAULT;
    public static BungeeTabListPlusHook BUNGEE_TAB_LIST_PLUS;
    public static MVdWPlaceholderAPIHook MVDW_PLACEHOLDER_API;
    public static PlaceholderAPIHook PLACEHOLDER_API;
    public static LeaderHeadsHook LEADER_HEADS;
    public static FunnyHologramManager HOLOGRAPHIC_DISPLAYS = new EmptyHologramManagerImpl();

    private final FunnyGuilds plugin;

    private final Map<String, PluginHook> pluginHooks = new HashMap<>();

    public HookManager(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void setupHooks() {
        WORLD_GUARD = setupHook("WorldGuard", pluginName -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");

                String worldGuardVersion = Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion();
                return worldGuardVersion.startsWith("7") ? new WorldGuard7Hook(pluginName) : new WorldGuard6Hook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                FunnyGuilds.getPluginLogger().warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
                return null;
            }
        });
        WORLD_EDIT = setupHook("WorldEdit", pluginName -> {
            try {
                Class.forName("com.sk89q.worldedit.Vector");
                return new WorldEdit6Hook(pluginName);
            }
            catch (ClassNotFoundException ignored) {
                return new WorldEdit7Hook(pluginName);
            }
        });
        FUNNY_TAB = setupHook("FunnyTab", pluginName -> new FunnyTabHook(pluginName, plugin), false);
        VAULT = setupHook("Vault", VaultHook::new);
        BUNGEE_TAB_LIST_PLUS = setupHook("BungeeTabListPlus", pluginName -> {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable");
                return new BungeeTabListPlusHook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                return null;
            }
        });
        MVDW_PLACEHOLDER_API = setupHook("MVdWPlaceholderAPI", pluginName -> {
            try {
                Class.forName("be.maximvdw.placeholderapi.PlaceholderReplacer");
                return new MVdWPlaceholderAPIHook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                return null;
            }
        });
        PLACEHOLDER_API = setupHook("PlaceholderAPI", PlaceholderAPIHook::new);
        LEADER_HEADS = setupHook("LeaderHeads", LeaderHeadsHook::new);
        HOLOGRAPHIC_DISPLAYS = setupHook("HolographicDisplays", pluginName -> new HolographicDisplaysHook(pluginName, plugin));
    }

    public <T extends PluginHook> T setupHook(String pluginName, Function<String, T> hookSupplier, boolean notifyIfMissing) {
        if (hookSupplier == null) {
            return null;
        }

        if (Bukkit.getPluginManager().getPlugin(pluginName) == null) {
            if (notifyIfMissing) {
                FunnyGuilds.getPluginLogger().info(pluginName + " plugin could not be found, some features may not be available");
            }
            return hookSupplier.apply(pluginName);
        }

        T hook = hookSupplier.apply(pluginName);
        if (hook == null) {
            return null;
        }

        hook.setPresent(true);
        this.pluginHooks.put(hook.getName(), hook);
        return hook;
    }

    public <T extends PluginHook> T setupHook(String pluginName, Function<String, T> hookSupplier) {
        return this.setupHook(pluginName, hookSupplier, true);
    }

    public void earlyInit() {
        pluginHooks.forEach((plugin, hook) -> {
            hook.earlyInit();
        });
    }

    public void init() {
        pluginHooks.forEach((plugin, hook) -> {
            hook.init();
        });
    }

}
