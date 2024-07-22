package net.dzikoysk.funnyguilds.legacy;

// https://github.com/PaperMC/paper-trail/blob/master/src/main/java/io/papermc/papertrail/Util.java
final class LegacyPluginUtil {
    static final String EQUALS_LINE = "====================================================";
    static final boolean PAPER = hasClass("com.destroystokyo.paper.PaperConfig")
        || hasClass("io.papermc.paper.configuration.Configuration");

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}