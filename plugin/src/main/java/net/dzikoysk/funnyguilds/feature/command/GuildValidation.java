package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;

public final class GuildValidation {

    private GuildValidation() {
    }

    public static Guild requireGuildByTag(String tag) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        return plugin.getGuildManager().findByTag(tag, true).orThrow(() -> {
            return new InternalValidationException(config -> config.commands.validation.guildWithTagNotExist, FunnyFormatter.of("{TAG}", tag));
        });
    }

}
