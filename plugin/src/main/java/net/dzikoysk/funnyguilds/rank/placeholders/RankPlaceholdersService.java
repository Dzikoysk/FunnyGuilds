package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.adventure.AdventureHelper;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.Pair;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");
    private static final Pattern TOP_POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankPlaceholdersService(
            PluginConfiguration config,
            MessageService messageService,
            UserRankManager userRankManager,
            GuildRankManager guildRankManager
    ) {
        this.config = config;
        this.messageService = messageService;
        this.userRankManager = userRankManager;
        this.guildRankManager = guildRankManager;
    }

    /**
     * Format top and top position placeholders in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    @Override
    public String format(@Nullable Object entity, String text, User targetUser) {
        if (entity == null) {
            entity = targetUser;
        }

        text = this.formatTop(text, targetUser);
        text = this.formatTopPosition(text, targetUser);
        return text;
    }

    /**
     * Format top placeholders (PTOP/GTOP-type-x) in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    public String formatTop(String text, @Nullable User targetUser) {
        if (text == null) {
            return "";
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = TOP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        String topType = matcher.group(1);
        String comparatorType = matcher.group(2);
        String indexString = matcher.group(3);

        Option<Integer> indexOption = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(indexString));
        if (indexOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
            return text;
        }

        int index = indexOption.get();
        if (index < 1) {
            FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
            return text;
        }

        if (topType.equalsIgnoreCase("PTOP")) {
            String placeholder = "{PTOP-" + comparatorType + "-" + index + "}";
            return this.userRankManager.getTop(comparatorType)
                    .flatMap(userTop -> userTop.getUser(index).map(user -> Pair.of(user, userTop)))
                    .map(pair -> {
                        User user = pair.getFirst();
                        Number topValue = pair.getSecond().getComparator().getValue(user.getRank());

                        String topFormat = this.config.top.format.ptop.getValue();
                        if (!topFormat.isEmpty()) {
                            List<RangeFormatting> formats = this.config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT));
                            topFormat = formatTopValue(topValue, topFormat, formats);
                        }
                        return this.formatUserRank(text, placeholder, user, topFormat);
                    })
                    .orElseGet(() -> FunnyFormatter.format(text, placeholder, this.messageService.<String>get(targetUser, config -> config.noValue.player.top)));
        }

        if (topType.equalsIgnoreCase("GTOP")) {
            String placeholder = "{GTOP-" + comparatorType + "-" + index + "}";
            return this.guildRankManager.getTop(comparatorType)
                    .flatMap(guildTop -> guildTop.getGuild(index).map(guild -> Pair.of(guild, guildTop)))
                    .map(pair -> {
                        Guild guild = pair.getFirst();
                        Number topValue = pair.getSecond().getComparator().getValue(guild.getRank());

                        String topFormat = this.config.top.format.gtop.getValue();
                        if (!topFormat.isEmpty()) {
                            List<RangeFormatting> formats = this.config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT));
                            topFormat = formatTopValue(topValue, topFormat, formats);
                        }
                        return this.formatGuildRank(text, placeholder, targetUser, guild, topFormat);
                    })
                    .orElseGet(() -> FunnyFormatter.format(text, placeholder, this.messageService.<String>get(targetUser, config -> config.noValue.guild.top)));
        }

        return text;
    }

    /**
     * Format top position placeholders (POSITION/G-POSITION-type) in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    public String formatTopPosition(String text, @Nullable User targetUser) {
        if (text == null) {
            return "";
        }

        if (!text.contains("POSITION-")) {
            return text;
        }

        Matcher matcher = TOP_POSITION_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        String positionType = matcher.group(1);
        String comparatorType = matcher.group(2);

        if (positionType.equalsIgnoreCase("POSITION")) {
            String placeholder = "{POSITION-" + comparatorType + "}";

            if (targetUser == null) {
                return FunnyFormatter.format(text, placeholder, 0);
            }

            int position = targetUser.getRank().getPosition(comparatorType);
            return FunnyFormatter.format(text, placeholder, position);
        }

        if (positionType.equalsIgnoreCase("G-POSITION")) {
            String replacement = Option.flatWhen(targetUser != null, targetUser::getGuild)
                    .filter(this.guildRankManager::isRankedGuild)
                    .map(guild -> guild.getRank().getPosition(comparatorType))
                    .map(Objects::toString)
                    .orElseGet(this.messageService.<String>get(targetUser, config -> config.noValue.guild.minMembersToInclude));

            return FunnyFormatter.format(text, "{G-POSITION-" + comparatorType + "}", replacement);
        }

        return text;
    }

    private static String formatTopValue(Number topValue, String topFormat, @Nullable List<RangeFormatting> formats) {
        String valueString = topValue instanceof Float || topValue instanceof Double
                ? String.format(Locale.US, "%.2f", topValue.floatValue())
                : topValue.toString();
        String valueFormat = formats == null
                ? valueString
                : NumberRange.inRangeToString(topValue, formats);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{VALUE-FORMAT}", valueFormat)
                .register("{VALUE}", valueString);

        return formatter.format(topFormat);
    }

    private String formatUserRank(String text, String placeholder, User user, String topFormat) {
        boolean online = user.isOnline();
        if (online && this.config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        RawString onlineColor = online ? this.config.ptopOnline : this.config.ptopOffline;
        return FunnyFormatter.format(text, placeholder, onlineColor + user.getName() + topFormat);
    }

    private String formatGuildRank(String text, String placeholder, @Nullable User targetUser, Guild guild, String topFormat) {
        String prefix = "{TAG}";

        if (this.config.top.useRelationshipColors) {
            Guild viewerGuild = targetUser != null ? targetUser.getGuild().orNull() : null;
            prefix = this.config.relationalTag.chooseAndPrepareTag(viewerGuild, guild);
        }

        String formattedPrefix = FunnyFormatter.format(prefix, "{TAG}", guild.getTag());
        return FunnyFormatter.format(text, placeholder, formattedPrefix + topFormat);
    }

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([A-Za-z0-9-_)]+)}");

    public Replaceable prepareReplacement(User targetUser) {
        return new Replaceable() {
            @Override
            public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
                return RankPlaceholdersService.this.format(targetUser, text, targetUser);
            }

            @Override
            public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
                TextReplacementConfig topReplacement = TextReplacementConfig.builder()
                        .match(PLACEHOLDER_PATTERN)
                        .replacement(((result, input) -> {
                            String replacement = RankPlaceholdersService.this.format(targetUser, result.group(), targetUser);
                            return AdventureHelper.legacyToComponent(replacement);
                        }))
                        .build();
                return text.replaceText(topReplacement);
            }
        };
    }

}
