package net.dzikoysk.funnyguilds.feature.hooks.placeholderapi;

import java.util.Locale;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public class PlaceholderAPIHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public PlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        new FunnyGuildsPlaceholder(this.plugin).register();
        return HookInitResult.SUCCESS;
    }

    public String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    public String replacePlaceholders(Player userOne, Player userTwo, String base) {
        return PlaceholderAPI.setRelationalPlaceholders(userOne, userTwo, base);
    }

    private static final class FunnyGuildsPlaceholder extends PlaceholderExpansion implements Relational {

        private final FunnyGuilds plugin;
        private final RankPlaceholdersService rankPlaceholdersService;
        private final String funnyguildsVersion;

        private FunnyGuildsPlaceholder(FunnyGuilds plugin) {
            this.plugin = plugin;
            this.rankPlaceholdersService = plugin.getRankPlaceholdersService();
            this.funnyguildsVersion = plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String identifier) {
            if (player == null) {
                return "";
            }

            Option<User> userOption = this.plugin.getUserManager().findByPlayer(player);
            if (userOption.isEmpty()) {
                return "";
            }

            User user = userOption.get();
            String lowerIdentifier = identifier.toLowerCase(Locale.ROOT);

            if (lowerIdentifier.contains("position-")) {
                return this.rankPlaceholdersService.formatTopPosition("{" + identifier.toUpperCase(Locale.ROOT) + "}", user);
            }
            else if (lowerIdentifier.contains("top-")) {
                return this.rankPlaceholdersService.formatTop("{" + identifier.toUpperCase(Locale.ROOT) + "}", user);
            }
            else {
                return this.plugin.getTablistPlaceholdersService().formatIdentifier(user, identifier, user);
            }
        }

        @Override // one - seeing the placeholder, two - about which the placeholder is
        public String onPlaceholderRequest(Player observer, Player target, String identifier) {
            if (observer == null || target == null || !identifier.equalsIgnoreCase("tag")) {
                return "";
            }

            UserManager userManager = this.plugin.getUserManager();
            Option<User> userObserverOption = userManager.findByPlayer(observer);
            Option<User> userTargetOption = userManager.findByPlayer(target);

            if (userObserverOption.isEmpty() || userTargetOption.isEmpty()) {
                return "";
            }

            return this.plugin.getPluginConfiguration().relationalTag.chooseAndPrepareTag(
                    userObserverOption.get().getGuild().orNull(),
                    userTargetOption.get().getGuild().orNull()
            );
        }

        @Override
        public @NotNull String getAuthor() {
            return "FunnyGuilds Team";
        }

        @Override
        public @NotNull String getIdentifier() {
            return "funnyguilds";
        }

        @Override
        public String getRequiredPlugin() {
            return "FunnyGuilds";
        }

        @Override
        public @NotNull String getVersion() {
            return this.funnyguildsVersion;
        }

        @Override
        public boolean persist() {
            return true;
        }
    }

}
