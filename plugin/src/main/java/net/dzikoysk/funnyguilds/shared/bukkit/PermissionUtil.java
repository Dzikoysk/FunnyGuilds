package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Map;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static <T> @Nullable T findHighestValue(Player player, Map<String, T> values, BiFunction<T, T, Boolean> checkIfHigher) {
        T highestValue = null;
        for (Map.Entry<String, T> permissionToValue : values.entrySet()) {
            String permission = permissionToValue.getKey();
            T value = permissionToValue.getValue();

            if (player.hasPermission(permission) && (highestValue == null || checkIfHigher.apply(highestValue, value))) {
                highestValue = value;
            }
        }
        return highestValue;
    }

    public static <T extends Comparable<T>> @Nullable T findHighestValue(Player player, Map<String, T> values) {
        return findHighestValue(player, values, (oldValue, newValue) -> newValue.compareTo(oldValue) < 0);
    }

}
