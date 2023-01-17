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
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.telemetry.FunnybinAsyncTask;
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
            case "funnybin":
                this.post(sender, args);
                break;
            case "help":
                this.messages.funnyguildsHelp.forEach(line -> this.sendMessage(sender, line));
                break;
            default:
                this.messageService.getMessage(config -> config.funnyguildsVersion)
                        .with(FunnyFormatter.of("{VERSION}", this.plugin.getVersion().getFullVersion()))
                        .send();
                break;
        }

    }

    private void saveAll(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);

        this.messageService.getMessage(config -> config.saveallSaving).sendTo(sender);
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
        this.messageService.getMessage(config -> config.saveallSaved)
                .with(FunnyFormatter.of("{TIME}", time))
                .sendTo(sender);
    }

    private void post(CommandSender sender, String[] args) {
        when(!sender.hasPermission("funnyguilds.admin"), config -> config.permission);

        FunnybinAsyncTask.of(sender, args)
                .onEmpty(() -> this.messages.funnybinHelp.forEach(line -> this.sendMessage(sender, line)))
                .peek(task -> this.plugin.scheduleFunnyTasks(task));
    }

    private void reload(CommandSender sender) {
        when(!sender.hasPermission("funnyguilds.reload"), config -> config.permission);

        this.messageService.getMessage(config -> config.reloadReloading).sendTo(sender);
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
            FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.reloadTime)
                    .with("{TIME}", time)
                    .receiver(this.sender)
                    .send();
        }

    }

}
