package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.time.ZoneId;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;

public class OffsetDateTimePlaceholders extends Placeholders<OffsetDateTime, OffsetDateTimePlaceholders> {

    protected final ZoneId timeZone;

    public OffsetDateTimePlaceholders(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public OffsetDateTimePlaceholders timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (entity, data) -> FunnyStringUtils.appendDigit(
                Objects.toString(timeResolver.resolve(
                        entity,
                        OffsetDateTime.now(this.timeZone)
                )))
        );
    }

    public OffsetDateTimePlaceholders timeProperty(String name, PairResolver<OffsetDateTime, Locale> timeResolver) {
        return this.property(name, (entity, data) -> {
            Locale locale = FunnyGuilds.getInstance().getMessageService().getLocale(entity);
            return Objects.toString(timeResolver.resolve(entity, data, locale));
        });
    }

    @Override
    public OffsetDateTimePlaceholders create() {
        return new OffsetDateTimePlaceholders(this.timeZone);
    }

}
