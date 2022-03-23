package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.TimePlaceholder;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.ChatColor;

public class Placeholders<T, P extends Placeholder<T>> {

    public static final Placeholders<LocalDateTime, TimePlaceholder> TIME_PLACEHOLDERS = new Placeholders<>();
    public static final Placeholders<Object, Placeholder<Object>> SIMPLE_PLACEHOLDERS = new Placeholders<>();
    public static final Placeholders<String, Placeholder<String>> ONLINE_PLACEHOLDERS = new Placeholders<>();

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        TIME_PLACEHOLDERS
                .property("hour", new TimePlaceholder(LocalDateTime::getHour))
                .property("minute", new TimePlaceholder(LocalDateTime::getMinute))
                .property("second", new TimePlaceholder(LocalDateTime::getSecond))
                .property("day_of_week", new TimePlaceholder(time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE)))
                .property("day_of_month", new TimePlaceholder(LocalDateTime::getDayOfMonth))
                .property("month", new TimePlaceholder(time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE)))
                .property("month_number", new TimePlaceholder(LocalDateTime::getMonthValue))
                .property("year", new TimePlaceholder(LocalDateTime::getYear));

        SIMPLE_PLACEHOLDERS
                .property("tps", object -> MinecraftServerUtils.getFormattedTPS());

        ONLINE_PLACEHOLDERS
                .raw("<online>", end -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    private final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public Map<String, P> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public P getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    public Placeholders<T, P> raw(String name, P placeholder){
        this.placeholders.put(name, placeholder);
        return this;
    }

    public Placeholders<T, P> property(String name, P placeholder) {
        return this.raw("{" + name.toUpperCase() + "}", placeholder);
    }

    public Placeholders<T, P> property(Collection<String> names, P placeholder) {
        names.forEach(name -> this.placeholders.put(name, placeholder));
        return this;
    }

    public String parse(String text, T object) {
        for (Entry<String, P> placeholder : placeholders.entrySet()) {
            text = text.replace(placeholder.getKey(), placeholder.getValue().get(object));
        }
        return text;
    }

}
