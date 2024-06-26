package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.stream.PandaStream;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class FunnyGuildsCommand extends AbstractFunnyCommand {

    @Inject
    public DataModel dataModel;

    @FunnyCommand(
            name = "${user.funnyguilds.name}",
            description = "${user.funnyguilds.description}",
            aliases = "${user.funnyguilds.aliases}",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";

        switch (parameter) {
            case "reload":
            case "rl":
                this.reload(sender);
                break;
            case "check":
            case "update":
                this.plugin.getVersion().isNewAvailable(sender, true);
                break;
            case "save-all":
                this.saveAll(sender);
                break;
            case "help":
                this.messageService.getMessage(config -> config.system.commandHelp)
                        .receiver(sender)
                        .send();
                break;
            default:
                this.messageService.getMessage(config -> config.system.pluginVersion)
                        .receiver(sender)
                        .with("{VERSION}", this.plugin.getVersion().getFullVersion())
                        .send();
                break;
        }

    }

    private void saveAll(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.commands.validation.noPermission);

        this.messageService.getMessage(config -> config.system.saveAllSaving)
                .receiver(sender)
                .send();
        Instant startTime = Instant.now();

        DataModel dataModel = this.dataModel;
        try {
            dataModel.save(false);
            this.plugin.getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("An error occurred while saving plugin data!", exception);
            return;
        }

        String time = TimeUtils.formatTimeSimple(Duration.between(startTime, Instant.now()));
        this.messageService.getMessage(config -> config.system.saveAllSaved)
                .receiver(sender)
                .with("{TIME}", time)
                .send();
    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), config -> config.commands.validation.noPermission);

        this.messageService.getMessage(config -> config.system.reloadReloading)
                .receiver(sender)
                .send();
        this.plugin.scheduleFunnyTasks(new ReloadAsyncTask(this.plugin, sender));
    }

    private static final class ReloadAsyncTask extends AsyncFunnyTask {

        private final FunnyGuilds plugin;
        private final CommandSender sender;
        private final Instant startTime;

        public ReloadAsyncTask(FunnyGuilds plugin, CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
            this.startTime = Instant.now();
        }

        @Override
        public void execute() {
            this.plugin.reloadConfiguration();
            this.plugin.getDataPersistenceHandler().reloadHandler();
            this.plugin.getDynamicListenerManager().reloadAll();

            if (this.plugin.getTablistConfiguration().enabled) {
                TablistConfiguration tablistConfig = this.plugin.getTablistConfiguration();
                UserManager userManager = this.plugin.getUserManager();

                PandaStream.of(Bukkit.getOnlinePlayers())
                        .flatMap(userManager::findByPlayer)
                        .forEach(user -> {
                            IndividualPlayerList playerList = new IndividualPlayerList(
                                    user,
                                    this.plugin.getNmsAccessor().getPlayerListAccessor(),
                                    this.plugin.getFunnyServer(),
                                    tablistConfig.cells,
                                    tablistConfig.header, tablistConfig.footer,
                                    tablistConfig.animated, tablistConfig.pages,
                                    tablistConfig.heads.textures,
                                    tablistConfig.cellsPing,
                                    tablistConfig.fillCells
                            );

                            user.getCache().setPlayerList(playerList);
                        });
            }

            String time = TimeUtils.formatTimeSimple(Duration.between(this.startTime, Instant.now()));
            FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.system.reloadTime)
                    .receiver(this.sender)
                    .with("{TIME}", time)
                    .send();
        }

    }

}
