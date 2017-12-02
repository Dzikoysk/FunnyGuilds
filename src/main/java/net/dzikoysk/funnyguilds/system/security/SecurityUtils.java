package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.util.StringUtils;

public final class SecurityUtils {

    private static final String SECURITY_TAG = "&c!!!&4[FunnyGuilds]&c!!! ";

    private SecurityUtils() {
    }

    public static String getSecurityTag() {
        return SECURITY_TAG;
    }

    public static String getBustedMessage(String name, String cheat) {
        return StringUtils.colored(
                getSecurityTag() +
                        "Przylapano gracza &d" + name +
                        " &cna uzywaniu cheatu &d" + cheat +
                        " &club innemu o podobnym dzialaniu."
        );
    }

    public static String getNoteMessage(String note) {
        return StringUtils.colored(
                getSecurityTag() +
                        "Notatka: &7" + note
        );
    }
}
