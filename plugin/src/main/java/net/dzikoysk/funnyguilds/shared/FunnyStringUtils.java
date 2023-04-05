package net.dzikoysk.funnyguilds.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import panda.utilities.text.Joiner;

public final class FunnyStringUtils {

    private FunnyStringUtils() {
    }

    public static String join(Collection<String> strings, boolean insertSpaces) {
        return join(strings, insertSpaces ? ", " : ",");
    }

    public static String join(Collection<String> strings) {
        return join(strings, false);
    }

    public static String join(Collection<String> strings, String separator) {
        return Joiner.on(separator).join(strings).toString();
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    @Contract("null, _, _ -> null; !null, null, _ -> !null;")
    public static String replace(@Nullable String string, @Nullable String pattern, String replacement) {
        if (isEmpty(string) || isEmpty(pattern))  {
            return string;
        }
        return string.replace(pattern, replacement);
    }

    public static String removeEnd(@Nullable String string, @Nullable String remove) {
        if (isEmpty(string) || isEmpty(remove)) {
            return string;
        }

        if (string.endsWith(remove)) {
            return string.substring(0, string.length() - remove.length());
        }

        return string;
    }

    public static List<String> fromString(String string) {
        List<String> list = new ArrayList<>();
        if (isEmpty(string)) {
            return list;
        }
        return Arrays.asList(string.split(","));
    }

    public static String appendDigit(int number) {
        return number > 9 ? Integer.toString(number) : "0" + number;
    }

    public static String appendDigit(String number) {
        return number.length() > 1 ? number : "0" + number;
    }

    public static String getPercent(double dividend, double divisor) {
        return getPercent(dividend / divisor);
    }

    public static String getPercent(double fraction) {
        return String.format(Locale.US, "%.1f", 100.0D * fraction);
    }

}
