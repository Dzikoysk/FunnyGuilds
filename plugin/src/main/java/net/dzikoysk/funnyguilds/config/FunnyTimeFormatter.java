package net.dzikoysk.funnyguilds.config;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FunnyTimeFormatter {

    private final String format;
    private final DateTimeFormatter formatter;

    public FunnyTimeFormatter(String format) {
        this.format = format;
        this.formatter = DateTimeFormatter.ofPattern(format, Locale.ROOT);
    }

    public String getFormat() {
        return this.format;
    }

    public String format(Instant instant, ZoneId timeZone) {
        return this.formatter.format(ZonedDateTime.ofInstant(
                instant,
                timeZone
        ));
    }

}
