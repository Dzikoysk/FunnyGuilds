package net.dzikoysk.funnyguilds.rank.placeholders;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class RankPlaceholdersService {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");
    private static final Pattern TOP_POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");
    private static final Pattern LEGACY_TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([0-9]+)}");

    private final FunnyGuildsLogger logger;

    private final PluginConfiguration config;
    private final MessageConfiguration messages;
    private final TablistConfiguration tablistConfig;

    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankPlaceholdersService(FunnyGuildsLogger logger, PluginConfiguration config, MessageConfiguration messages, TablistConfiguration tablistConfig, UserRankManager userRankManager, GuildRankManager guildRankManager) {
        this.logger = logger;

        this.config = config;
        this.messages = messages;
        this.tablistConfig = tablistConfig;

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
    public String format(String text, User targetUser) {
        text = formatTop(text, targetUser);
        text = formatTopPosition(text, targetUser);
        if (config.top.enableLegacyPlaceholders) {
            text = formatRank(text, targetUser);
        }
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
            return null;
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = TOP_PATTERN.matcher(text);
        if (matcher.find()) {
            String topType = matcher.group(1);
            String comparatorType = matcher.group(2);
            String indexString = matcher.group(3);

            int index;
            try {
                index = Integer.parseInt(indexString);
            }
            catch (NumberFormatException ex) {
                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                return text;
            }

            if (index < 1) {
                FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                return text;
            }

            if (topType.equalsIgnoreCase("PTOP")) {
                Option<UserTop> userTopOption = userRankManager.getTop(comparatorType);
                if (userTopOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + comparatorType + "-" + index + "}", messages.ptopNoValue);
                }
                UserTop userTop = userTopOption.get();

                Option<User> userOption = userTop.getUser(index);
                if (userOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + comparatorType + "-" + index + "}", messages.ptopNoValue);
                }
                User user = userOption.get();

                Number topValue = userTop.getComparator().getValue(user.getRank());
                String topFormat = config.top.format.ptop.getValue();
                if (!topFormat.isEmpty()) {
                    List<RangeFormatting> valueFormatting = config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase());
                    topFormat = topFormat.replace("{VALUE-FORMAT}", valueFormatting == null
                            ? topValue.toString()
                            : NumberRange.inRangeToString(topValue, valueFormatting));
                    topFormat = topFormat.replace("{VALUE}", topValue.toString());
                }

                return this.formatUserRank(text, "{PTOP-" + comparatorType + "-" + index + "}", user, topFormat);
            }
            else if (topType.equalsIgnoreCase("GTOP")) {
                Option<GuildTop> guildTopOption = guildRankManager.getTop(comparatorType);
                if (guildTopOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + comparatorType + "-" + index + "}", messages.gtopNoValue);
                }
                GuildTop guildTop = guildTopOption.get();

                Option<Guild> guildOption = guildTop.getGuild(index);
                if (guildOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + comparatorType + "-" + index + "}", messages.gtopNoValue);
                }
                Guild guild = guildOption.get();

                Number topValue = guildTop.getComparator().getValue(guild.getRank());
                String topFormat = config.top.format.gtop.getValue();
                List<RangeFormatting> valueFormatting = config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase());
                topFormat = topFormat.replace("{VALUE-FORMAT}", valueFormatting == null
                        ? topValue.toString()
                        : NumberRange.inRangeToString(topValue, valueFormatting));
                topFormat = topFormat.replace("{VALUE}", topValue.toString());

                return this.formatGuildRank(text, "{GTOP-" + comparatorType + "-" + index + "}", targetUser, guild, topFormat);
            }
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
            return null;
        }

        if (!text.contains("POSITION-")) {
            return text;
        }

        Matcher matcher = TOP_POSITION_PATTERN.matcher(text);
        if (matcher.find()) {
            String positionType = matcher.group(1);
            String comparatorType = matcher.group(2);

            if (positionType.equalsIgnoreCase("POSITION")) {
                if (targetUser == null) {
                    return StringUtils.replace(text, "{POSITION}", "0");
                }
                return StringUtils.replace(text, "{POSITION-" + comparatorType + "}", Integer.toString(targetUser.getRank().getPosition(comparatorType)));
            }
            else if (positionType.equalsIgnoreCase("G-POSITION")) {
                if (targetUser == null) {
                    return StringUtils.replace(text, "{POSITION}", messages.minMembersToIncludeNoValue);
                }

                Option<Guild> guildOption = targetUser.getGuild();
                if (guildOption.isEmpty()) {
                    return StringUtils.replace(text, "{G-POSITION-" + comparatorType + "}", messages.minMembersToIncludeNoValue);
                }
                Guild guild = guildOption.get();

                return StringUtils.replace(text, "{G-POSITION-" + comparatorType + "}", guildRankManager.isRankedGuild(guild)
                        ? Integer.toString(guild.getRank().getPosition(comparatorType))
                        : messages.minMembersToIncludeNoValue);
            }
        }

        return text;
    }

    // TODO Migrate all {PTOP/GTOP-x} placeholders to new {PTOP/GTOP-type-x} and remove this method
    /**
     * Format legacy top placeholders (PTOP/GTOP-x) in text
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    public String formatRank(String text, @Nullable User targetUser) {
        if (text == null) {
            return null;
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = LEGACY_TOP_PATTERN.matcher(text);
        if (matcher.find()) {
            String topType = matcher.group(1);
            String indexString = matcher.group(2);

            int index;
            try {
                index = Integer.parseInt(indexString);
            }
            catch (NumberFormatException ex) {
                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                return text;
            }

            if (index < 1) {
                FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                return text;
            }

            if (topType.equalsIgnoreCase("PTOP")) {
                Option<User> userOption = userRankManager.getUser(DefaultTops.USER_POINTS_TOP, index);
                if (userOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + index + "}", messages.ptopNoValue);
                }
                User user = userOption.get();

                int points = user.getRank().getPoints();
                String pointsFormat = config.ptopPoints.getValue();
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return this.formatUserRank(text, "{PTOP-" + index + "}", user, pointsFormat);
            }
            else if (topType.equalsIgnoreCase("GTOP")) {
                Option<Guild> guildOption = guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, index);
                if (guildOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + index + "}", messages.gtopNoValue);
                }
                Guild guild = guildOption.get();

                int points = guild.getRank().getAveragePoints();
                String pointsFormat = config.gtopPoints.getValue();
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return this.formatGuildRank(text, "{GTOP-" + index + "}", targetUser, guild, pointsFormat);
            }
        }

        return text;
    }

    private String formatUserRank(String text, String placeholder, User user, String topFormat) {
        boolean online = user.isOnline();
        if (online && config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        return StringUtils.replace(text, placeholder, (online ? config.ptopOnline : config.ptopOffline) + user.getName() + topFormat);
    }

    private String formatGuildRank(String text, String placeholder, @Nullable User targetUser, Guild guild, String topFormat) {
        String guildTag = guild.getTag();
        if (tablistConfig.playerListUseRelationshipColors) {
            guildTag = StringUtils.replace(config.prefixOther.getValue(), "{TAG}", guild.getTag());
            if (targetUser != null && targetUser.hasGuild()) {
                Guild sourceGuild = targetUser.getGuild().get();

                if (sourceGuild.getAllies().contains(guild)) {
                    guildTag = StringUtils.replace(config.prefixAllies.getValue(), "{TAG}", guild.getTag());
                }
                else if (sourceGuild.getEnemies().contains(guild)) {
                    guildTag = StringUtils.replace(config.prefixEnemies.getValue(), "{TAG}", guild.getTag());
                }
                else if (sourceGuild.getUUID().equals(guild.getUUID())) {
                    guildTag = StringUtils.replace(config.prefixOur.getValue(), "{TAG}", guild.getTag());
                }
            }
        }

        return StringUtils.replace(text, placeholder, guildTag + topFormat);
    }

}
