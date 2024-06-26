package net.dzikoysk.funnyguilds.shared;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;
public final class Validate {

    private Validate() {
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isFalse(boolean condition, String message) {
        isTrue(!condition, message);
    }

    public static <T> T notNull(@Nullable T object, String message) {
        return Objects.requireNonNull(object, message);
    }

    public static String notBlank(@Nullable String string, String message) {
        Objects.requireNonNull(string, message);
        if (string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return string;
    }

}
