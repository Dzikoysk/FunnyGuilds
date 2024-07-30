package net.dzikoysk.funnyguilds.shared.adventure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

final class LegacyItemHoverEventHelper {

    private static Class<?> NAMESPACED_KEY;
    private static Method GET_NAMESPACED_KEY;
    private static Method GET_NAMESPACE;
    private static Method GET_KEY;

    private static final Class<?> NMS_ITEM_STACK = Reflections.getNMSClass("ItemStack", "world.item");
    private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY = Reflections.getMethod(Reflections.getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class);
    private static final Method NMS_ITEM_STACK_GET_TAG = Reflections.getMethod(NMS_ITEM_STACK, "getTag");

    static {
        if (!Reflections.USE_PRE_13_METHODS) {
            NAMESPACED_KEY = Reflections.getBukkitClass("NamespacedKey");
            GET_NAMESPACED_KEY = Reflections.getMethod(Material.class, "getKey");
            GET_NAMESPACE = Reflections.getMethod(NAMESPACED_KEY, "getNamespace");
            GET_KEY = Reflections.getMethod(NAMESPACED_KEY, "getKey");
        }
    }

    private LegacyItemHoverEventHelper() {
    }

    static HoverEventSource<?> getHoverForItem(ItemStack item) {
        try {
            HoverEvent.ShowItem showItem = HoverEvent.ShowItem.showItem(getMaterialKey(item.getType()), item.getAmount(), getBinaryTagHolder(item));
            return HoverEvent.showItem(showItem);
        } catch (Exception ignored) {
            return null;
        }
    }

    static Key getMaterialKey(Material material) throws InvocationTargetException, IllegalAccessException {
        if (NAMESPACED_KEY == null || GET_NAMESPACED_KEY == null) {
            return Key.key(material.name().toLowerCase());
        }

        Object namespacedKey = GET_NAMESPACED_KEY.invoke(material);
        String namespace = (String) GET_NAMESPACE.invoke(namespacedKey);
        String key = (String) GET_KEY.invoke(namespacedKey);

        return Key.key(namespace, key);
    }

    @Nullable
    static Object getTagCompound(ItemStack item) {
        Object nbtTagCompound;
        try {
            Object nsmItemStack = CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);
            nbtTagCompound = NMS_ITEM_STACK_GET_TAG.invoke(nsmItemStack);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
        return nbtTagCompound;
    }

    @Nullable
    static BinaryTagHolder getBinaryTagHolder(ItemStack item) {
        Object nbtTagCompound = getTagCompound(item);
        if (nbtTagCompound == null) {
            return null;
        }
        return BinaryTagHolder.binaryTagHolder(nbtTagCompound.toString());
    }

}
