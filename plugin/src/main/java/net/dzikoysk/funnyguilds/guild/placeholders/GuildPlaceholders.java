package net.dzikoysk.funnyguilds.guild.placeholders;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocalePairResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.shared.TimeUtils;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholders> {

    public GuildPlaceholders property(String name, LocaleMonoResolver<Guild> resolver, LocaleSimpleResolver fallbackResolver) {
        return this.property(name, new FallbackPlaceholder<>(resolver, fallbackResolver));
    }

    public GuildPlaceholders property(String name, MonoResolver<Guild> resolver, LocaleSimpleResolver fallbackResolve) {
        return this.property(name, (entity, data) -> resolver.resolve(data), fallbackResolve);
    }

    public GuildPlaceholders rankProperty(String name, LocalePairResolver<Guild, GuildRank> resolver, LocaleSimpleResolver fallbackResolver) {
        return this.property(name, (entity, guild) -> resolver.resolve(entity, guild, guild.getRank()), fallbackResolver);
    }

    public GuildPlaceholders rankProperty(String name, MonoResolver<GuildRank> resolver, Number fallbackValue) {
        return this.property(name,
                (entity, guild) -> {
                    Object value = resolver.resolve(guild.getRank());
                    if (value instanceof Float || value instanceof Double) {
                        return String.format("%.2f", ((Number) value).floatValue());
                    }
                    return Objects.toString(value);
                },
                entity -> fallbackValue
        );
    }

    public GuildPlaceholders timeProperty(
            String name,
            Function<Guild, Instant> timeSupplier,
            ZoneId timeZone,
            MessageService messages,
            Function<MessageConfiguration, String> fallbackSupplier
    ) {
        FunnyTimeFormatter dateFormat = messages.get(config -> config.dateFormat);
        String noValue = Objects.toString(messages.get(fallbackSupplier), "");
        SimpleResolver fallbackResolver = () -> noValue;
        return this.property(name, (entity, guild) -> formatDate(guild, timeSupplier, timeZone, messages.get(entity, config -> config.dateFormat), noValue), fallbackResolver)
                .property(name + "-time", (entity, guild) -> formatTime(guild, timeSupplier, noValue), fallbackResolver);
    }

    private static String formatDate(
            Guild guild,
            Function<Guild, Instant> timeSupplier,
            ZoneId timeZone,
            FunnyTimeFormatter formatter,
            String noValue
    ) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : formatter.format(endTime, timeZone);
    }

    private static String formatTime(Guild guild, Function<Guild, Instant> timeSupplier, String noValue) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : TimeUtils.formatTime(Duration.between(Instant.now(), endTime));
    }

    @Override
    public GuildPlaceholders create() {
        return new GuildPlaceholders();
    }

}
