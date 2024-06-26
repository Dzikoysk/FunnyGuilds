package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.BukkitMessageService;
import dev.peri.yetanothermessageslibrary.SimpleSendableMessageService;
import dev.peri.yetanothermessageslibrary.config.serdes.SerdesMessages;
import dev.peri.yetanothermessageslibrary.viewer.ViewerFactory;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import java.io.IOException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.serdes.DecolorTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeFormatterTransformer;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

public class MessageService extends SimpleSendableMessageService<CommandSender, MessageConfiguration, FunnyMessageDispatcher> {

    public MessageService(FunnyGuilds plugin) {
        super(
                new BukkitViewerDataSupplier(),
                ViewerFactory.create(BukkitMessageService.wrapScheduler(plugin)),
                (viewerService, localeSupplier, messageSupplier) -> new FunnyMessageDispatcher(viewerService, localeSupplier, messageSupplier, user -> Bukkit.getPlayer(user.getUUID()))
        );
    }

    public void reload() {
        this.getMessageRepositories().forEach((locale, repository) -> repository.load());
    }

    public void playerQuit(Player player) {
        this.getViewerService().removeViewer(player);
    }

    public static MessageService prepareMessageService(FunnyGuilds plugin, File languageFolder) {
        FunnyGuildsLogger logger = plugin.getPluginLogger();
        PluginConfiguration config = plugin.getPluginConfiguration();

        MessageService messageService = new MessageService(plugin);
        messageService.setDefaultLocale(config.defaultLocale);
        messageService.registerLocaleProvider(new CommandSenderLocaleProvider());
        messageService.registerLocaleProvider(new UserLocaleProvider(plugin.getFunnyServer()));

        PandaStream.of(config.availableLocales).forEach(locale -> {
            String localeName = locale.toString();
            File localeFile = new File(languageFolder, localeName + ".yml");
            if (!localeFile.exists()) {
                try {
                    FunnyIOUtils.copyFileFromResources(FunnyGuilds.class.getResourceAsStream("/lang/" + localeName + ".yml"), localeFile, true);
                } catch (IOException | NullPointerException ex) {
                    logger.warning("Could not copy default language file: " + localeName);
                    logger.warning("New language file will be created with default values");
                }
            }
            messageService.registerRepository(locale, createMessageConfiguration(localeFile));
        });
        return messageService;
    }

    private static MessageConfiguration createMessageConfiguration(File messageConfigurationFile) {
        return ConfigManager.create(MessageConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withSerdesPack(registry -> {
                registry.register(new DecolorTransformer());
                registry.register(new FunnyTimeFormatterTransformer());
                registry.register(new SerdesMessages());
            });

            it.withBindFile(messageConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

}
