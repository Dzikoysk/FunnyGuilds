package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

public final class GuildUtils {

    private GuildUtils() {
    }

    /**
     * Gets the set of guild tags from collection of guild.
     *
     * @param guilds collection of users
     * @return set of guild tags
     */
    public static Set<String> getTags(Collection<Guild> guilds) {
        return guilds.stream()
                .filter(Objects::nonNull)
                .map(Guild::getTag)
                .collect(Collectors.toSet());
    }

    /**
     * Validate guild name.
     *
     * @param pluginConfiguration the PluginConfiguration from which pattern will be used
     * @param guildName           guild name to validate
     * @return if guild name is valid
     */
    public static boolean validateName(PluginConfiguration pluginConfiguration, String guildName) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildNames.stream()
                .anyMatch(name -> name.equalsIgnoreCase(guildName));
    }

    /**
     * Validate guild tag.
     *
     * @param pluginConfiguration the PluginConfiguration from which pattern will be used
     * @param guildTag            guild tag to validate
     * @return if guild tag is valid
     */
    public static boolean validateTag(PluginConfiguration pluginConfiguration, String guildTag) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildTags.stream()
                .anyMatch(tag -> tag.equalsIgnoreCase(guildTag));
    }

}
