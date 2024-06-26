package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static <T> @Nullable T findHighestValue(CommandSender sender, String permissionPrefix, Map<String, T> values, Comparator<T> comparator) {
        T highestValue = null;
        for (Map.Entry<String, T> permissionToValue : values.entrySet()) {
            String permission = permissionPrefix + "." + permissionToValue.getKey();
            T value = permissionToValue.getValue();

            if (sender.hasPermission(permission) && (highestValue == null || comparator.compare(value, highestValue) > 0)){
                highestValue = value;
            }
        }
        return highestValue;
    }

    public static <T extends Comparable<T>> @Nullable T findHighestValue(CommandSender sender, String permissionPrefix, Map<String, T> values) {
        return findHighestValue(sender, permissionPrefix, values, Comparator.naturalOrder());
    }

}
