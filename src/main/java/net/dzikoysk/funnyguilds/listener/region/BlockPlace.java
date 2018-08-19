package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlace implements Listener {

    private final PluginConfig config = Settings.getConfig();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getBlock().getLocation(), true)) {
            if (config.buggedBlocks && !config.buggedBlocksExclude.contains(e.getBlock().getType())) {
                ItemStack returnItem = e.getPlayer().getItemInHand().clone();
                returnItem.setAmount(1);
                Bukkit.getScheduler().runTaskLater(FunnyGuilds.getInstance(), () -> {
                    e.getBlockReplacedState().update(true);

                    if (config.buggedBlockReturn) {
                        e.getPlayer().getInventory().addItem(returnItem);
                    }
                }, config.buggedBlocksTimer);

                return;
            }

            e.setCancelled(true);
        }
    }

}
