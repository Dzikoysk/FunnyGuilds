package net.dzikoysk.funnyguilds.system.protection;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public final class ProtectionUtils {

    private ProtectionUtils() {
    }

    public static boolean action(Action action, Block block) {
        return (action == Action.RIGHT_CLICK_BLOCK) && checkBlock(block);
    }

    private static boolean checkBlock(Block block) {

        Region region = RegionUtils.getAt(block.getLocation());

        if (region == null) {
            return false;
        }

        switch (block.getType()) {
            case CHEST:
            case ENCHANTMENT_TABLE:
            case FURNACE:
            case ENDER_CHEST:
            case WORKBENCH:
            case ANVIL:
            case HOPPER:
                return true;
            default:
                return false;
        }
    }
}
