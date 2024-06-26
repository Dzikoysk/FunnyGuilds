package net.dzikoysk.funnyguilds.feature.hooks.vault;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.shared.Validate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultHook extends AbstractPluginHook {

    private static Economy economyHook;
    private static Permission permissionHook;

    public VaultHook(String name) {
        super(name);
    }

    @Override
    public HookInitResult init() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        RegisteredServiceProvider<Economy> economyProvider = servicesManager.getRegistration(Economy.class);
        RegisteredServiceProvider<Permission> permissionProvider = servicesManager.getRegistration(Permission.class);

        if (economyProvider != null) {
            economyHook = economyProvider.getProvider();
        }
        else {
            FunnyGuilds.getPluginLogger().warning("Vault - No economy provider found, some features may not be available");
        }

        if (permissionProvider != null) {
            permissionHook = permissionProvider.getProvider();
        }
        else {
            FunnyGuilds.getPluginLogger().warning("Vault - No permission provider found, some features may not be available");
        }

        return HookInitResult.SUCCESS;
    }

    public static boolean isEconomyHooked() {
        return economyHook != null;
    }

    public static double accountBalance(Player player) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.getBalance(player);
    }

    public static boolean canAfford(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.has(player, money);
    }

    public static EconomyResponse withdrawFromPlayerBank(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.withdrawPlayer(player, money);
    }

    public static boolean hasPermission(OfflinePlayer player, String permission) {
        return permissionHook != null && permissionHook.playerHas(null, player, permission);
    }

}
