package net.dzikoysk.funnyguilds.guild.placeholders;

import java.util.LinkedHashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholders;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.StaticPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.Pair;
import panda.utilities.StringUtils;

public class GuildPlaceholdersService extends StaticPlaceholdersService<Guild, GuildPlaceholders> {

    public static final BasicPlaceholders<Pair<String, Guild>> GUILD_MEMBERS_COLOR_CONTEXT = new BasicPlaceholders<Pair<String, Guild>>()
            .property("members", pair -> {
                String text = JOIN_OR_DEFAULT.apply(UserUtils.getOnlineNames(pair.getSecond().getMembers()), "");

                return !text.contains("<online>")
                        ? text
                        : BasicPlaceholdersService.ONLINE.toFormatter(pair.getFirst()).format(text);
            });

    private static Option<GuildPlaceholders> SIMPLE = Option.none();

    @Override
    public String format(@Nullable Object entity, String text, Guild guild) {
        text = super.format(entity, text, guild);
        text = GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatUtils.getLastColorBefore(text, "{MEMBERS}"), guild))
                .format(text);
        return text;
    }

    public static Option<GuildPlaceholders> getSimplePlaceholders() {
        return SIMPLE;
    }

    public static GuildPlaceholders createSimplePlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, entity -> messages.get(entity, config -> config.noValue.guild.name))
                .property("guild", Guild::getName, entity -> messages.get(entity, config -> config.noValue.guild.name))
                .property("tag", Guild::getTag, entity -> messages.get(entity, config -> config.noValue.guild.tag));
        SIMPLE = Option.of(placeholders);

        return placeholders;
    }

    public static GuildPlaceholders createGuildPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageService messages = plugin.getMessageService();
        GuildRankManager rankManager = plugin.getGuildRankManager();

        return new GuildPlaceholders()
                .property("owner", guild -> guild.getOwner().getName(), entity -> messages.get(entity, config -> config.noValue.guild.owner))
                .property("deputies",
                        (entity, guild) -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getDeputies()), messages.get(entity, config -> config.noValue.guild.deputies)),
                        entity -> messages.get(entity, config -> config.noValue.guild.deputies))
                .property("deputy",
                        (entity, guild) -> guild.getDeputies().isEmpty()
                                ? messages.get(entity, config -> config.noValue.guild.deputy)
                                : guild.getDeputies().iterator().next().getName(),
                        entity -> messages.get(entity, config -> config.noValue.guild.deputy))
                .property("members-online", guild -> guild.getOnlineMembers().size(), entity -> 0)
                .property("members-all", guild -> guild.getMembers().size(), entity -> 0)
                .property("allies-all", guild -> guild.getAllies().size(), entity -> 0)
                .property("enemies-all", guild -> guild.getEnemies().size(), entity -> 0)
                .property("region-size",
                        (entity, guild) -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElseGet(messages.<String>get(entity, config -> config.noValue.guild.regionSize)),
                        entity -> messages.get(entity, config -> config.noValue.guild.regionSize))
                .property("pvp",
                        (entity, guild) -> guild.hasPvPEnabled()
                                ? messages.get(entity, config -> config.guild.commands.pvp.enabledStatus)
                                : messages.get(entity, config -> config.guild.commands.pvp.disabledStatus),
                        entity -> messages.get(entity, config -> config.guild.commands.pvp.disabledStatus))
                .timeProperty("validity", Guild::getValidity, pluginConfiguration.timeZone, messages, config -> config.noValue.guild.validity)
                .timeProperty("protection", Guild::getProtection, pluginConfiguration.timeZone, messages, config -> config.noValue.guild.protection)
                .property("lives", Guild::getLives, entity -> 0)
                .property("lives-symbol",
                        guild -> {
                            int lives = guild.getLives();
                            if (lives <= pluginConfiguration.warLives) {
                                return StringUtils.repeated(lives, pluginConfiguration.livesRepeatingSymbol.full.getValue()) +
                                        StringUtils.repeated(pluginConfiguration.warLives - lives, pluginConfiguration.livesRepeatingSymbol.empty.getValue());
                            } else {
                                return StringUtils.repeated(pluginConfiguration.warLives, pluginConfiguration.livesRepeatingSymbol.full.getValue()) +
                                        pluginConfiguration.livesRepeatingSymbol.more.getValue();
                            }
                        }, entity -> messages.get(config -> config.noValue.guild.lives))
                .property("lives-symbol-all",
                        guild -> StringUtils.repeated(guild.getLives(), pluginConfiguration.livesRepeatingSymbol.full.getValue()),
                        entity -> messages.get(entity, config -> config.noValue.guild.lives))
                .rankProperty("points", GuildRank::getAveragePoints, 0)
                .rankProperty("avg-points", GuildRank::getAveragePoints,0)
                .rankProperty("avg-points-format",
                        (entity, guild, rank) -> FunnyFormatter.format(NumberRange.inRangeToString(rank.getAveragePoints(),
                                pluginConfiguration.pointsFormat), "{POINTS}", guild.getRank().getAveragePoints()),
                        entity -> FunnyFormatter.format(NumberRange.inRangeToString(0, pluginConfiguration.pointsFormat), "{POINTS}", 0))
                .rankProperty("kills", GuildRank::getKills, 0)
                .rankProperty("avg-kills", GuildRank::getAverageKills, 0)
                .rankProperty("deaths", GuildRank::getDeaths, 0)
                .rankProperty("avg-deaths", GuildRank::getAverageDeaths, 0)
                .rankProperty("assists", GuildRank::getAssists, 0)
                .rankProperty("avg-assists", GuildRank::getAverageAssists, 0)
                .rankProperty("logouts", GuildRank::getLogouts, 0)
                .rankProperty("avg-logouts", GuildRank::getAverageLogouts, 0)
                .rankProperty("kdr", GuildRank::getKDR, 0.00)
                .rankProperty("avg-kdr", GuildRank::getAverageKDR, 0.00)
                .rankProperty("kda", GuildRank::getKDA, 0.00)
                .rankProperty("avg-kda", GuildRank::getAverageKDA, 0.00);
    }

    public static GuildPlaceholders createAlliesEnemiesPlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();
        return new GuildPlaceholders()
                .property("allies",
                        (entity, guild) -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getAllies()), messages.get(entity, config -> config.noValue.guild.allies)),
                        entity -> messages.get(entity, config -> config.noValue.guild.allies))
                .property("allies-tags",
                        (entity, guild) -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getAllies()), messages.get(entity, config -> config.noValue.guild.allies)),
                        entity -> messages.get(entity, config -> config.noValue.guild.allies))
                .property("enemies",
                        (entity, guild) -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getEnemies()), messages.get(entity, config -> config.noValue.guild.enemies)),
                        entity -> messages.get(entity, config -> config.noValue.guild.enemies))
                .property("enemies-tags",
                        (entity, guild) -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getEnemies()), messages.get(entity, config -> config.noValue.guild.enemies)),
                        entity -> messages.get(entity, config -> config.noValue.guild.enemies));
    }

    @Override
    public Set<FunnyFormatter> prepareReplacements(@Nullable Object entity, Guild data) {
        Set<FunnyFormatter> formatters = new LinkedHashSet<>(super.prepareReplacements(entity, data));
        formatters.add(GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatColor.RESET.toString(), data)));
        return formatters;
    }

}
