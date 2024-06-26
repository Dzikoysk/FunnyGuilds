package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.serdes.EntityTypeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyPatternTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.ItemStackTransformer;
import net.dzikoysk.funnyguilds.config.serdes.MaterialTransformer;
import net.dzikoysk.funnyguilds.config.serdes.NumberRangeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RangeFormattingTransformer;
import net.dzikoysk.funnyguilds.config.serdes.SkinTextureSerializer;
import net.dzikoysk.funnyguilds.config.serdes.VectorSerializer;
import net.dzikoysk.funnyguilds.config.serdes.ZoneIdTransformer;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistPageSerializer;
import net.dzikoysk.funnyguilds.feature.command.config.CommandConfiguration;
import net.dzikoysk.funnyguilds.guild.config.GuildConfiguration;
import net.dzikoysk.funnyguilds.user.config.UserConfiguration;

public final class ConfigurationFactory {

    private ConfigurationFactory() {
    }

    private static <C extends ConfigSection> C create(
            Class<C> configClass,
            File file,
            Consumer<C> consumer
    ) {
        C config = ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new ZoneIdTransformer());
                registry.register(new FunnyTimeTransformer());
                registry.register(new FunnyPatternTransformer());
                registry.register(new NumberRangeTransformer());
                registry.register(new RangeFormattingTransformer());
            });
            it.withBindFile(file);
            it.withLogger(FunnyGuilds.getInstance().getLogger());
            it.saveDefaults();
            it.load(true);
        });
        consumer.accept(config);
        return config;
    }

    private static <C extends ConfigSection> C createBukkit(
            Class<C> configClass,
            File file,
            Consumer<C> consumer
    ) {
        return create(configClass, file, (it) -> {
            it.withSerdesPack(registry -> {
                registry.register(new MaterialTransformer());
                registry.register(new ItemStackTransformer());
                registry.register(new EntityTypeTransformer());
                registry.register(new VectorSerializer());
            });
            consumer.accept(it);
        });
    }

    public static PluginConfiguration createPluginConfiguration(File pluginConfigurationFile) {
        return createBukkit(PluginConfiguration.class, pluginConfigurationFile, (it) -> {
        });
    }

    public static CommandConfiguration createCommandConfiguration(File configurationFile) {
        return create(CommandConfiguration.class, configurationFile, (it) -> {
        });
    }

    public static UserConfiguration createUserConfiguration(File userConfigurationFile) {
        return createBukkit(UserConfiguration.class, userConfigurationFile, (it) -> {
        });
    }

    public static GuildConfiguration createGuildConfiguration(File guildConfigurationFile) {
        return createBukkit(GuildConfiguration.class, guildConfigurationFile, (it) -> {
        });
    }

    public static TablistConfiguration createTablistConfiguration(File tablistConfigurationFile) {
        return create(TablistConfiguration.class, tablistConfigurationFile, (it) -> {
            it.withSerdesPack(registry -> {
                registry.register(new TablistPageSerializer());
                registry.register(new SkinTextureSerializer());
            });
        });
    }

}
