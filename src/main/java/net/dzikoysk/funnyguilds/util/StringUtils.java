package net.dzikoysk.funnyguilds.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class StringUtils {

    public static String replace(String text, String searchString, String replacement) {
        if (text == null || text.isEmpty() || searchString.isEmpty()) {
            return text;
        }

        if (replacement == null) {
            replacement = "";
        }

        int start = 0;
        int max = -1;
        int end = text.indexOf(searchString, start);

        if (end == -1) {
            return text;
        }

        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= 16;
        StringBuilder sb = new StringBuilder(text.length() + increase);

        while (end != -1) {
            sb.append(text.substring(start, end)).append(replacement);
            start = end + replLength;

            if (--max == 0) {
                break;
            }

            end = text.indexOf(searchString, start);
        }

        sb.append(text.substring(start));
        return sb.toString();
    }

    public static String colored(String s) {
        return s != null ? ChatColor.translateAlternateColorCodes('&', s) : null;
    }
    
    public static List<String> colored(List<String> list) {
        List<String> colored = new ArrayList<String>();
        
        for (String s : list) {
            colored.add(colored(s));
        }
        
        return colored;
    }

    public static String toString(Collection<String> list, boolean send) {
        StringBuilder builder = new StringBuilder();

        for (String s : list) {
            builder.append(s);
            builder.append(',');

            if (send) {
                builder.append(' ');
            }
        }

        String s = builder.toString();

        if (send) {
            if (s.length() > 2) {
                s = s.substring(0, s.length() - 2);
            }
            
            else if (s.length() > 1) {
                s = s.substring(0, s.length() - 1);
            }
        }

        return s;
    }

    public static List<String> fromString(String s) {
        List<String> list = new ArrayList<>();

        if (s == null || s.isEmpty()) {
            return list;
        }

        list = Arrays.asList(s.split(","));
        return list;
    }

    public static String appendDigit(int number) {
        return number > 9 ? "" + number : "0" + number;
    }

    public static String getPercent(double dividend, double divisor) {
        return getPercent(dividend / divisor);
    }
    
    public static String getPercent(double fraction) {
        return String.format(Locale.US, "%.1f", 100.0D * fraction);
    }
    
    private StringUtils() {}

}
