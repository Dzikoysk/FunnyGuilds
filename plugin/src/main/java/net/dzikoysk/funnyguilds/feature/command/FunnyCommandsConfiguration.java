package net.dzikoysk.funnyguilds.feature.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.resources.types.PlayerType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.command.config.CommandConfiguration;
import net.dzikoysk.funnyguilds.feature.command.config.CommandRegister;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Server;
import panda.utilities.text.Joiner;

public final class FunnyCommandsConfiguration {

    private FunnyCommandsConfiguration() {
    }

    public static FunnyCommands createFunnyCommands(FunnyGuilds plugin) {
        Server server = plugin.getServer();

        PluginConfiguration config = plugin.getPluginConfiguration();
        CommandConfiguration commands = plugin.getCommandConfiguration();

        // User commands
        CommandConfiguration.UserCommands userCommands = commands.user;

        CommandConfiguration.FunnyCommand enlargeCommand = userCommands.guild.enlarge;
        enlargeCommand.enabled = enlargeCommand.enabled && !plugin.getGuildConfiguration().region.enlarge.requirements.isEmpty();

        CommandComponents generalComponents = CommandComponents.collect("user.general", userCommands.general);
        CommandComponents playerComponents = CommandComponents.collect("user.player", userCommands.player);
        CommandComponents guildComponents = CommandComponents.collect("user.guild", userCommands.guild);
        CommandComponents adminComponents = CommandComponents.collect("admin", commands.admin);

        UserManager userManager = plugin.getUserManager();
        GuildManager guildManager = plugin.getGuildManager();

        return FunnyCommands.configuration(() -> plugin)
                .registerDefaultComponents()
                .placeholders(generalComponents.placeholders)
                .placeholders(playerComponents.placeholders)
                .placeholders(guildComponents.placeholders)
                .placeholders(adminComponents.placeholders)
                .injector(plugin.getInjector().fork(resources -> {
                }))
                .bind(new UserBind(userManager))
                .bind(new GuildBind(userManager))
                .type(new PlayerType(server))
                .completer(new MembersCompleter(userManager))
                .completer(new GuildsCompleter(guildManager))
                .completer(new AlliesCompleter(userManager))
                .completer(new GuildInvitationsCompleter(userManager, plugin.getGuildInvitationList()))
                .completer(new InvitePlayersCompleter(config, userManager))
                .validator(new MemberValidator())
                .validator(new ManageValidator())
                .validator(new OwnerValidator())
                .commands(generalComponents.commands)
                .commands(playerComponents.commands)
                .commands(guildComponents.commands)
                .commands(adminComponents.commands)
                .exceptionHandler(new InternalValidationExceptionHandler(plugin.getMessageService()))
                .exceptionHandler(new FunnyGuildsExceptionHandler(FunnyGuilds.getPluginLogger()))
                .install();
    }

    private static final class CommandComponents {

        private final String group;
        private final Map<String, Function<String, String>> placeholders = new HashMap<>();
        private final List<Class<?>> commands = new ArrayList<>();

        private CommandComponents(String group) {
            this.group = group;
        }

        private CommandComponents command(String name, CommandConfiguration.FunnyCommand configuration, Class<?> command) {
            if (configuration.enabled) {
                this.placeholders.put(this.group + "." + name + ".name", key -> configuration.name);
                this.placeholders.put(this.group + "." + name + ".aliases", key -> Joiner.on(", ").join(configuration.aliases).toString());
                this.placeholders.put(this.group + "." + name + ".description", key -> "");
                this.commands.add(command);
            }

            return this;
        }

        private CommandComponents command(String name, String alias, Class<?> command) {
            this.placeholders.put(this.group + "." + name + ".name", key -> alias);
            this.commands.add(command);
            return this;
        }

        private static CommandComponents collect(String group, Object toCollect) {
            CommandComponents components = new CommandComponents(group);

            Class<?> toCollectClass = toCollect.getClass();
            for (Field field : toCollectClass.getDeclaredFields()) {
                try {
                    CommandRegister registerAnnotation = field.getAnnotation(CommandRegister.class);
                    if (registerAnnotation == null) {
                        continue;
                    }

                    Object fieldValue = field.get(toCollect);
                    if (!(fieldValue instanceof CommandConfiguration.FunnyCommand command)) {
                        continue;
                    }

                    components.command(registerAnnotation.key(), command, registerAnnotation.as());
                } catch (IllegalAccessException ex) {
                    FunnyGuilds.getPluginLogger().error("Could not collect command components", ex);
                    return null;
                }
            }

            return components;
        }

    }

}
