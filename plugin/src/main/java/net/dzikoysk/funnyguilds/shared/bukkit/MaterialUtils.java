package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Locale;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import panda.std.stream.PandaStream;

public final class MaterialUtils {

    private MaterialUtils() {
    }

    @Contract("_, false -> !null")
    public static Material parseMaterial(String materialString, boolean allowNullReturn) {
        if (materialString == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material: null");
            return allowNullReturn ? null : Material.AIR;
        }

        String materialName = FunnyFormatter.format(materialString.toUpperCase(Locale.ROOT), " ", "_");
        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown material: " + materialString);
            return allowNullReturn ? null : Material.AIR;
        }

        return material;
    }

    public static Set<Material> parseMaterials(boolean allowNullReturn, String... materialStrings) {
        return PandaStream.of(materialStrings)
                .map(materialString -> parseMaterial(materialString, allowNullReturn))
                .toSet();
    }

    public static String getMaterialName(Material material) {
        return FunnyFormatter.format(material.toString().toLowerCase(Locale.ROOT), "_", " ");
    }

    public static String getItemCustomName(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return "";
        }

        if (!itemMeta.hasDisplayName()) {
            return "";
        }

        return itemMeta.getDisplayName();
    }

}
