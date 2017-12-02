package net.dzikoysk.funnyguilds.util.element.tablist;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.NotificationUtil;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import net.dzikoysk.funnyguilds.util.runnable.Ticker;

public abstract class AbstractTablist {

    private static final Set<AbstractTablist> TABLIST_CACHE = new HashSet<>();

    protected final Map<Integer, String> tablistPattern;
    protected final String header;
    protected final String footer;
    protected final UUID player;
    protected final int ping;
    protected boolean firstPacket = true;

    public AbstractTablist(Map<Integer, String> tablistPattern, String header, String footer, int ping, Player player) {
        this.tablistPattern = tablistPattern;
        this.header = header;
        this.footer = footer;
        this.ping = ping;
        this.player = player.getUniqueId();
    }

    public static void wipeCache() {
        TABLIST_CACHE.clear();
    }

    public static AbstractTablist createTablist(Map<Integer, String> pattern, String header, String footer, int ping, Player player) {

        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        if ("v1_8_R1".equals(Reflections.getFixedVersion())) {
            AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R1.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        }
        if ("v1_8_R2".equals(Reflections.getFixedVersion()) || "v1_8_R3".equals(Reflections.getFixedVersion()) || "v1_9_R1".equals(Reflections.getFixedVersion()) || "v1_9_R2".equals(Reflections.getFixedVersion())) {
            AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R3.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        } else if ("v1_10_R1".equals(Reflections.getFixedVersion()) || "v1_11_R1".equals(Reflections.getFixedVersion()) || "v1_12_R1".equals(Reflections.getFixedVersion())) {
            AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_10_R1.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        } else {
            throw new RuntimeException("Could not find tablist for given version.");
        }
    }

    public static AbstractTablist getTablist(Player player) {

        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        throw new IllegalStateException("Given player's tablist does not exist!");
    }

    public static void removeTablist(Player player) {

        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                TABLIST_CACHE.remove(tablist);
                break;
            }
        }
    }

    public static boolean hasTablist(Player player) {

        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public abstract void send();

    protected void sendPackets(List<Object> packets) {

        Player target = Bukkit.getPlayer(player);

        if (target == null) {
            return;
        }

        PacketSender.sendPacket(target, packets);
    }

    protected Object createBaseComponent(String text, boolean keepNewLines) {
        return NotificationUtil.createBaseComponent(text, keepNewLines);
    }

    protected String putVars(String cell) {

        String formatted = cell;
        User user = User.get(player);

        if (user == null) {
            throw new IllegalStateException("Given player is null!");
        }

        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        if (hour < 10) {
            formatted = StringUtils.replace(formatted, "{HOUR}", "0" + String.valueOf(hour));
        } else {
            formatted = StringUtils.replace(formatted, "{HOUR}", String.valueOf(hour));
        }

        if (minute < 10) {
            formatted = StringUtils.replace(formatted, "{MINUTE}", "0" + String.valueOf(minute));
        } else {
            formatted = StringUtils.replace(formatted, "{MINUTE}", String.valueOf(minute));
        }

        if (second < 10) {
            formatted = StringUtils.replace(formatted, "{SECOND}", "0" + String.valueOf(second));
        } else {
            formatted = StringUtils.replace(formatted, "{SECOND}", String.valueOf(second));
        }

        if (user.hasGuild()) {
            Guild guild = user.getGuild();
            formatted = StringUtils.replace(formatted, "{G-NAME}", guild.getName());
            formatted = StringUtils.replace(formatted, "{G-TAG}", guild.getTag());
            formatted = StringUtils.replace(formatted, "{G-OWNER}", guild.getOwner().getName());
            formatted = StringUtils.replace(formatted, "{G-LIVES}", String.valueOf(guild.getLives()));
            formatted = StringUtils.replace(formatted, "{G-ALLIES}", String.valueOf(guild.getAllies().size()));
            formatted = StringUtils.replace(formatted, "{G-POINTS}", String.valueOf(guild.getRank().getPoints()));
            formatted = StringUtils.replace(formatted, "{G-KILLS}", String.valueOf(guild.getRank().getKills()));
            formatted = StringUtils.replace(formatted, "{G-DEATHS}", String.valueOf(guild.getRank().getDeaths()));
            formatted = StringUtils.replace(formatted, "{G-MEMBERS-ONLINE}", String.valueOf(guild.getOnlineMembers().size()));
            formatted = StringUtils.replace(formatted, "{G-MEMBERS-ALL}", String.valueOf(guild.getMembers().size()));
            formatted = StringUtils.replace(formatted, "{G-POSITION}", (guild.getMembers().size() >= Settings.getConfig().minMembersToInclude)
                    ? String.valueOf(guild.getRank().getPosition()) : Settings.getConfig().minMembersPositionString);
        } else {
            formatted = StringUtils.replace(formatted, "{G-NAME}", "Brak");
            formatted = StringUtils.replace(formatted, "{G-TAG}", "Brak");
            formatted = StringUtils.replace(formatted, "{G-OWNER}", "Brak");
            formatted = StringUtils.replace(formatted, "{G-LIVES}", "0");
            formatted = StringUtils.replace(formatted, "{G-ALLIES}", "0");
            formatted = StringUtils.replace(formatted, "{G-POINTS}", "0");
            formatted = StringUtils.replace(formatted, "{G-KILLS}", "0");
            formatted = StringUtils.replace(formatted, "{G-DEATHS}", "0");
            formatted = StringUtils.replace(formatted, "{G-MEMBERS-ONLINE}", "0");
            formatted = StringUtils.replace(formatted, "{G-MEMBERS-ALL}", "0");
            formatted = StringUtils.replace(formatted, "{G-POSITION}", Settings.getConfig().minMembersPositionString);
        }

        formatted = StringUtils.replace(formatted, "{PLAYER}", user.getName());
        formatted = StringUtils.replace(formatted, "{PING}", String.valueOf((double) user.getPing()));
        formatted = StringUtils.replace(formatted, "{POINTS}", String.valueOf(user.getRank().getPoints()));
        formatted = StringUtils.replace(formatted, "{POSITION}", String.valueOf(user.getRank().getPosition()));
        formatted = StringUtils.replace(formatted, "{KILLS}", String.valueOf(user.getRank().getKills()));
        formatted = StringUtils.replace(formatted, "{DEATHS}", String.valueOf(user.getRank().getDeaths()));

        formatted = StringUtils.replace(formatted, "{ONLINE}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        formatted = StringUtils.replace(formatted, "{TPS}", Ticker.getRecentTPS(0));

        formatted = StringUtils.colored(formatted);

        String temp = Parser.parseRank(formatted);
        if (temp != null) {
            formatted = temp;
        }

        return formatted;
    }

    @Deprecated
    protected String putHeaderFooterVars(String text) {

        String formatted = text;
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        if (hour < 10) {
            formatted = StringUtils.replace(formatted, "{HOUR}", "0" + String.valueOf(hour));
        } else {
            formatted = StringUtils.replace(formatted, "{HOUR}", String.valueOf(hour));
        }

        if (minute < 10) {
            formatted = StringUtils.replace(formatted, "{MINUTE}", "0" + String.valueOf(minute));
        } else {
            formatted = StringUtils.replace(formatted, "{MINUTE}", String.valueOf(minute));
        }

        if (second < 10) {
            formatted = StringUtils.replace(formatted, "{SECOND}", "0" + String.valueOf(second));
        } else {
            formatted = StringUtils.replace(formatted, "{SECOND}", String.valueOf(second));
        }

        formatted = StringUtils.colored(formatted);

        return formatted;
    }

    protected boolean shouldUseHeaderAndFooter() {
        return !this.header.isEmpty() || !this.footer.isEmpty();
    }
}
