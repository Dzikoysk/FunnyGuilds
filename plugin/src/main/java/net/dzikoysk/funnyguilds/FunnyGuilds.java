package net.dzikoysk.funnyguilds;

import com.google.common.collect.ImmutableSet;
import eu.okaeri.configs.exception.OkaeriException;
import java.io.File;
import java.lang.reflect.Method;
import me.pikamug.localelib.LocaleManager;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler;
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.feature.command.FunnyCommandsConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateSyncTask;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.DummyManager;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.feature.tablist.TablistPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.validity.GuildValidationHandler;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.listener.BlockFlow;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PistonUse;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.TntProtection;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.EntityPlace;
import net.dzikoysk.funnyguilds.listener.region.EntityProtect;
import net.dzikoysk.funnyguilds.listener.region.GuildHeartProtectionHandler;
import net.dzikoysk.funnyguilds.listener.region.HangingBreak;
import net.dzikoysk.funnyguilds.listener.region.HangingPlace;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn;
import net.dzikoysk.funnyguilds.listener.region.PlayerTeleport;
import net.dzikoysk.funnyguilds.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.FunnyTask;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;
import net.dzikoysk.funnyguilds.telemetry.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import panda.std.Option;
import panda.std.Result;
import panda.utilities.ClassUtils;

import static java.lang.String.format;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds plugin;
    private static FunnyGuildsLogger logger;

    private final File pluginConfigurationFile = new File(this.getDataFolder(), "config.yml");
    private final File tablistConfigurationFile = new File(this.getDataFolder(), "tablist.yml");
    private final File pluginLanguageFolderFile = new File(this.getDataFolder(), "lang");
    private final File pluginDataFolderFile = new File(this.getDataFolder(), "data");

    private FunnyGuildsVersion version;
    private FunnyCommands funnyCommands;

    private PluginConfiguration pluginConfiguration;
    private TablistConfiguration tablistConfiguration;

    private MessageService messageService;
    private LocaleManager localeManager;

    private DynamicListenerManager dynamicListenerManager;
    private HookManager hookManager;
    private UserManager userManager;
    private GuildManager guildManager;
    private UserRankManager userRankManager;
    private GuildRankManager guildRankManager;
    private DamageManager damageManager;
    private RegionManager regionManager;
    private FunnyServer funnyServer;

    private Option<IndividualNameTagManager> individualNameTagManager = Option.none();
    private Option<DummyManager> dummyManager = Option.none();

    private GuildInvitationList guildInvitationList;
    private AllyInvitationList allyInvitationList;

    private BasicPlaceholdersService basicPlaceholdersService;
    private TimePlaceholdersService timePlaceholdersService;
    private UserPlaceholdersService userPlaceholdersService;
    private GuildPlaceholdersService guildPlaceholdersService;
    private RankPlaceholdersService rankPlaceholdersService;
    private TablistPlaceholdersService tablistPlaceholdersService;

    private NmsAccessor nmsAccessor;
    private GuildEntityHelper guildEntityHelper;

    private Option<Database> database = Option.none();
    private DataModel dataModel;
    private DataPersistenceHandler dataPersistenceHandler;
    private InvitationPersistenceHandler invitationPersistenceHandler;

    private Injector injector;

    private volatile BukkitTask guildValidationTask;
    private volatile BukkitTask tablistBroadcastTask;
    private volatile BukkitTask rankRecalculationTask;

    private volatile Option<BukkitTask> nameTagUpdateTask = Option.none();
    private volatile Option<BukkitTask> dummyUpdateTask = Option.none();
    private volatile Option<BukkitTask> scoreboardQueueUpdateTask = Option.none();

    private boolean isDisabling;
    private boolean forceDisabling;

    @Override
    public void onLoad() {
        Reflections.prepareServerVersion();

        plugin = this;
        logger = new FunnyGuildsLogger.DefaultLogger(this);
        this.version = new FunnyGuildsVersion(this);
        this.funnyServer = new FunnyServer(this.getServer());

        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        }
        catch (Exception spigotNeeded) {
            logger.error("FunnyGuilds requires spigot to work, your server seems to be using something else");
            logger.error("If you think that is not true - contact plugin developers");
            logger.error("https://github.com/FunnyGuilds/FunnyGuilds");

            this.shutdown("Spigot required for service not detected!");
            return;
        }

        Result<File, String> createResult = FunnyIOUtils.createFile(this.getDataFolder(), true);
        if (createResult.isErr()) {
            this.shutdown(createResult.getError());
            return;
        }

        try {
            this.pluginConfiguration = ConfigurationFactory.createPluginConfiguration(this.pluginConfigurationFile);
            this.tablistConfiguration = ConfigurationFactory.createTablistConfiguration(this.tablistConfigurationFile);
        }
        catch (Exception exception) {
            logger.error("Could not load plugin configuration", exception);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        try {
            this.nmsAccessor = prepareNmsAccessor();
        }
        catch (Throwable th) {
            String currentVersion = "Unknown";
            try {
                Method getMinecraftVersion = Server.class.getMethod("getMinecraftVersion");
                currentVersion = (String) getMinecraftVersion.invoke(this.getServer());
            } catch (Throwable ignored) {
                try {
                    currentVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                } catch (Throwable ignored2) { }
            }

            logger.error(format("Version '%s' is not supported yet, please reach us on issue tracker or on Discord that can be found here: https://github.com/FunnyGuilds/FunnyGuilds", currentVersion), th);
            this.shutdown("Critical error has been encountered!");
            return;
        }
        this.guildEntityHelper = new GuildEntityHelper(this.pluginConfiguration, this.nmsAccessor);

        DescriptionChanger descriptionChanger = new DescriptionChanger(super.getDescription());
        descriptionChanger.rename(this.pluginConfiguration.pluginName);

        this.dynamicListenerManager = new DynamicListenerManager(this);

        this.hookManager = new HookManager(plugin);
        this.hookManager.setupEarlyHooks();
        this.hookManager.earlyInit();
    }

    @Override
    public void onEnable() {
        if (this.forceDisabling) {
            return;
        }

        try {
            Result<File, String> createResult = FunnyIOUtils.createFile(this.pluginLanguageFolderFile, true);
            if (createResult.isErr()) {
                this.shutdown(createResult.getError());
                return;
            }
            this.messageService = MessageService.prepareMessageService(this, this.pluginLanguageFolderFile);
        }
        catch (Exception exception) {
            logger.error("Could not initialize message service", exception);
            this.shutdown("Critical error has been encountered!");
            return;
        }
        this.localeManager = new LocaleManager();
        this.userManager = new UserManager(this.pluginConfiguration);
        this.guildManager = new GuildManager(this.pluginConfiguration);
        this.userRankManager = new UserRankManager(this.pluginConfiguration);
        this.userRankManager.register(DefaultTops.defaultUserTops(this.pluginConfiguration, this.userManager));
        this.guildRankManager = new GuildRankManager(this.pluginConfiguration);
        this.guildRankManager.register(DefaultTops.defaultGuildTops(this.guildManager));
        this.damageManager = new DamageManager();
        this.regionManager = new RegionManager(this.pluginConfiguration);

        this.prepareScoreboardServices();

        this.guildInvitationList = new GuildInvitationList(this.userManager, this.guildManager);
        this.allyInvitationList = new AllyInvitationList(this.guildManager);

        this.basicPlaceholdersService = new BasicPlaceholdersService();
        this.basicPlaceholdersService.register(this, "simple", BasicPlaceholdersService.createSimplePlaceholders(this));

        this.timePlaceholdersService = new TimePlaceholdersService();
        this.timePlaceholdersService.register(this, "time", TimePlaceholdersService.createTimePlaceholders());

        this.userPlaceholdersService = new UserPlaceholdersService();
        this.userPlaceholdersService.register(this, "player", UserPlaceholdersService.createPlayerPlaceholders(this));
        this.userPlaceholdersService.register(this, "user", UserPlaceholdersService.createUserPlaceholders(this));

        this.guildPlaceholdersService = new GuildPlaceholdersService();
        this.guildPlaceholdersService.register(this, "simple", GuildPlaceholdersService.createSimplePlaceholders(this));
        this.guildPlaceholdersService.register(this, "guild", GuildPlaceholdersService.createGuildPlaceholders(this));
        this.guildPlaceholdersService.register(this, "allies_enemies", GuildPlaceholdersService.createAlliesEnemiesPlaceholders(this));

        this.rankPlaceholdersService = new RankPlaceholdersService(
                this.pluginConfiguration,
                this.tablistConfiguration,
                this.messageService,
                this.userRankManager,
                this.guildRankManager
        );
        this.tablistPlaceholdersService = new TablistPlaceholdersService(
                this.basicPlaceholdersService,
                this.timePlaceholdersService,
                this.userPlaceholdersService,
                this.guildPlaceholdersService
        );

        if (pluginConfiguration.dataModel.isSQL()) {
            try {
                this.database = Option.of(new Database());
            } catch (Exception ex) {
                logger.error("Could not create data from database", ex);
                this.shutdown("Critical error has been encountered!");
                return;
            }
        }

        try {
            this.dataModel = DataModel.create(this, this.pluginConfiguration.dataModel);
            this.dataModel.load();
        }
        catch (Exception ex) {
            logger.error("Could not load data from database", ex);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        this.dataPersistenceHandler = new DataPersistenceHandler(this);
        this.dataPersistenceHandler.startHandler();

        this.invitationPersistenceHandler = new InvitationPersistenceHandler(this);
        this.invitationPersistenceHandler.loadInvitations();
        this.invitationPersistenceHandler.startHandler();

        this.injector = DependencyInjection.createInjector(resources -> {
            resources.on(Server.class).assignInstance(this.getServer());
            resources.on(FunnyServer.class).assignInstance(this.funnyServer);
            resources.on(FunnyGuilds.class).assignInstance(this);
            resources.on(FunnyGuildsLogger.class).assignInstance(FunnyGuilds::getPluginLogger);
            resources.on(PluginConfiguration.class).assignInstance(this.pluginConfiguration);
            resources.on(TablistConfiguration.class).assignInstance(this.tablistConfiguration);
            resources.on(MessageService.class).assignInstance(this.messageService);
            resources.on(UserManager.class).assignInstance(this.userManager);
            resources.on(GuildManager.class).assignInstance(this.guildManager);
            resources.on(UserRankManager.class).assignInstance(this.userRankManager);
            resources.on(GuildRankManager.class).assignInstance(this.guildRankManager);
            resources.on(RegionManager.class).assignInstance(this.regionManager);
            resources.on(DamageManager.class).assignInstance(this.damageManager);
            resources.on(GuildInvitationList.class).assignInstance(this.guildInvitationList);
            resources.on(AllyInvitationList.class).assignInstance(this.allyInvitationList);
            resources.on(BasicPlaceholdersService.class).assignInstance(this.basicPlaceholdersService);
            resources.on(TimePlaceholdersService.class).assignInstance(this.timePlaceholdersService);
            resources.on(UserPlaceholdersService.class).assignInstance(this.userPlaceholdersService);
            resources.on(GuildPlaceholdersService.class).assignInstance(this.guildPlaceholdersService);
            resources.on(RankPlaceholdersService.class).assignInstance(this.rankPlaceholdersService);
            resources.on(NmsAccessor.class).assignInstance(this.nmsAccessor);
            resources.on(GuildEntityHelper.class).assignInstance(this.guildEntityHelper);
            resources.on(DataModel.class).assignInstance(this.dataModel);
        });

        MetricsCollector collector = new MetricsCollector(this);
        collector.start();

        this.guildValidationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GuildValidationHandler(this), 100L, 20L);
        this.tablistBroadcastTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TablistBroadcastHandler(this), 20L, this.tablistConfiguration.updateInterval);
        this.rankRecalculationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(this), 20L, this.pluginConfiguration.rankingUpdateInterval);

        try {
            this.funnyCommands = FunnyCommandsConfiguration.createFunnyCommands(this);
        }
        catch (Exception exception) {
            logger.error("Could not register commands", exception);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            ImmutableSet.Builder<Class<? extends Listener>> setBuilder = ImmutableSet.builder();

            setBuilder
                    .add(GuiActionHandler.class)
                    .add(EntityDamage.class)
                    .add(EntityInteract.class)
                    .add(PlayerChat.class)
                    .add(PlayerDeath.class)
                    .add(PlayerJoin.class)
                    .add(PlayerLogin.class)
                    .add(PlayerQuit.class)
                    .add(GuildHeartProtectionHandler.class)
                    .add(TntProtection.class);

            if (this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.blockFlow) {
                setBuilder.add(BlockFlow.class);
            }

            if (ClassUtils.forName("org.bukkit.event.entity.EntityPlaceEvent").isPresent()) {
                setBuilder.add(EntityPlace.class);
            }
            else {
                logger.warning("Cannot register EntityPlaceEvent listener on this version of server");
            }

            for (Class<? extends Listener> listenerClass : setBuilder.build()) {
                pluginManager.registerEvents(this.injector.newInstanceWithFields(listenerClass), this);
            }

            this.dynamicListenerManager.registerDynamic(() -> this.pluginConfiguration.regionsEnabled,
                    this.injector.newInstanceWithFields(BlockBreak.class),
                    this.injector.newInstanceWithFields(BlockIgnite.class),
                    this.injector.newInstanceWithFields(BlockPlace.class),
                    this.injector.newInstanceWithFields(PistonUse.class),
                    this.injector.newInstanceWithFields(BucketAction.class),
                    this.injector.newInstanceWithFields(EntityExplode.class),
                    this.injector.newInstanceWithFields(HangingBreak.class),
                    this.injector.newInstanceWithFields(HangingPlace.class),
                    this.injector.newInstanceWithFields(PlayerCommand.class),
                    this.injector.newInstanceWithFields(PlayerInteract.class),
                    this.injector.newInstanceWithFields(EntityProtect.class)
            );

            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventMove,
                    this.injector.newInstanceWithFields(PlayerMove.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventPhysics,
                    this.injector.newInstanceWithFields(BlockPhysics.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.respawnInBase,
                    this.injector.newInstanceWithFields(PlayerRespawn.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventTeleport,
                    this.injector.newInstanceWithFields(PlayerTeleport.class)
            );

            this.dynamicListenerManager.reloadAll();
        }
        catch (Throwable throwable) {
            logger.error("Could not register listeners", throwable);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        this.handleReload();

        this.version.isNewAvailable(this.getServer().getConsoleSender(), true);
        this.hookManager.setupHooks();
        this.hookManager.init();

        if (NmsUtils.getReloadCount() > 0) {
            this.messageService.getMessage(config -> config.reloadWarn)
                    .broadcast()
                    .permission("funnyguilds.admin")
                    .send();
        }

        logger.info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        if (this.forceDisabling) {
            return;
        }

        this.isDisabling = true;

        this.funnyCommands.dispose();
        this.dynamicListenerManager.unregisterAll();
        this.guildEntityHelper.despawnGuildEntities(this.guildManager);

        this.guildValidationTask.cancel();
        this.tablistBroadcastTask.cancel();
        this.rankRecalculationTask.cancel();

        this.dataModel.save(false);
        this.dataPersistenceHandler.stopHandler();

        this.invitationPersistenceHandler.saveInvitations();
        this.invitationPersistenceHandler.stopHandler();

        this.getServer().getScheduler().cancelTasks(this);
        this.database.peek(Database::shutdown);

        this.messageService.close();

        plugin = null;
    }

    public void shutdown(String content) {
        if (this.isDisabling || this.forceDisabling) {
            return;
        }

        this.forceDisabling = true;
        logger.warning("The FunnyGuilds is going to shut down! " + content);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    private void handleReload() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            Option<User> userOption = this.userManager.findByPlayer(player);
            if (userOption.isEmpty()) {
                continue;
            }
            User user = userOption.get();

            FunnyGuildsInboundChannelHandler inboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallInboundChannelHandler(player);
            inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(plugin, user));

            FunnyGuildsOutboundChannelHandler outboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallOutboundChannelHandler(player);
            outboundChannelHandler.getPacketSuppliersRegistry().setOwner(player);
            outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.guildEntityHelper));

            if (!this.tablistConfiguration.enabled) {
                continue;
            }

            IndividualPlayerList individualPlayerList = new IndividualPlayerList(
                    user,
                    this.nmsAccessor.getPlayerListAccessor(),
                    this.funnyServer,
                    this.tablistConfiguration.cells,
                    this.tablistConfiguration.header, this.tablistConfiguration.footer,
                    this.tablistConfiguration.animated, this.tablistConfiguration.pages,
                    this.tablistConfiguration.heads.textures,
                    this.tablistConfiguration.cellsPing,
                    this.tablistConfiguration.fillCells
            );

            user.getCache().setPlayerList(individualPlayerList);
        }

        this.getIndividualNameTagManager().map(ScoreboardGlobalUpdateSyncTask::new).peek(this::scheduleFunnyTasks);
        this.getDummyManager().map(ScoreboardGlobalUpdateSyncTask::new).peek(this::scheduleFunnyTasks);

        this.guildEntityHelper.spawnGuildEntities(this.guildManager);
    }

    public void scheduleFunnyTasks(FunnyTask... tasks) {
        for (FunnyTask task : tasks) {
            switch (task.getType()) {
                case SYNC:
                    Bukkit.getScheduler().runTask(this, task);
                    break;
                case ASYNC:
                    Bukkit.getScheduler().runTaskAsynchronously(this, task);
                    break;
            }
        }
    }

    public boolean isDisabling() {
        return this.isDisabling;
    }

    public FunnyGuildsVersion getVersion() {
        return this.version;
    }

    public File getPluginDataFolder() {
        return this.pluginDataFolderFile;
    }

    public Option<Database> getDatabase() {
        return this.database;
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    public DataPersistenceHandler getDataPersistenceHandler() {
        return this.dataPersistenceHandler;
    }

    public InvitationPersistenceHandler getInvitationPersistenceHandler() {
        return this.invitationPersistenceHandler;
    }

    public PluginConfiguration getPluginConfiguration() {
        return this.pluginConfiguration;
    }

    public File getPluginConfigurationFile() {
        return this.pluginConfigurationFile;
    }

    public TablistConfiguration getTablistConfiguration() {
        return this.tablistConfiguration;
    }

    public File getTablistConfigurationFile() {
        return this.tablistConfigurationFile;
    }

    public MessageService getMessageService() {
        return this.messageService;
    }

    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    public DynamicListenerManager getDynamicListenerManager() {
        return this.dynamicListenerManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public GuildManager getGuildManager() {
        return this.guildManager;
    }

    public UserRankManager getUserRankManager() {
        return this.userRankManager;
    }

    public GuildRankManager getGuildRankManager() {
        return this.guildRankManager;
    }

    public DamageManager getDamageManager() {
        return this.damageManager;
    }

    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    public FunnyServer getFunnyServer() {
        return this.funnyServer;
    }

    public Option<IndividualNameTagManager> getIndividualNameTagManager() {
        return this.individualNameTagManager;
    }

    public Option<DummyManager> getDummyManager() {
        return this.dummyManager;
    }

    public GuildInvitationList getGuildInvitationList() {
        return this.guildInvitationList;
    }

    public AllyInvitationList getAllyInvitationList() {
        return this.allyInvitationList;
    }

    public BasicPlaceholdersService getBasicPlaceholdersService() {
        return this.basicPlaceholdersService;
    }

    public TimePlaceholdersService getTimePlaceholdersService() {
        return this.timePlaceholdersService;
    }

    public UserPlaceholdersService getUserPlaceholdersService() {
        return this.userPlaceholdersService;
    }

    public GuildPlaceholdersService getGuildPlaceholdersService() {
        return this.guildPlaceholdersService;
    }

    public RankPlaceholdersService getRankPlaceholdersService() {
        return this.rankPlaceholdersService;
    }

    public TablistPlaceholdersService getTablistPlaceholdersService() {
        return this.tablistPlaceholdersService;
    }

    public NmsAccessor getNmsAccessor() {
        return this.nmsAccessor;
    }

    public GuildEntityHelper getGuildEntityHelper() {
        return this.guildEntityHelper;
    }

    public Injector getInjector() {
        return this.injector;
    }

    public void reloadConfiguration() throws OkaeriException {
        this.pluginConfiguration.load();
        this.tablistConfiguration.load();
        this.messageService.reload();
        this.hookManager.callConfigUpdated();
        this.prepareScoreboardServices();
    }

    private void prepareScoreboardServices() {
        this.nameTagUpdateTask.peek(BukkitTask::cancel);
        this.dummyUpdateTask.peek(BukkitTask::cancel);
        this.scoreboardQueueUpdateTask.peek(BukkitTask::cancel);

        ScoreboardConfiguration scoreboardConfig = this.pluginConfiguration.scoreboard;
        if (!scoreboardConfig.enabled) {
            return;
        }
        ScoreboardService scoreboardService = new ScoreboardService(this.pluginConfiguration);

        this.individualNameTagManager = Option.when(
                scoreboardConfig.nametag.enabled,
                () -> new IndividualNameTagManager(this.pluginConfiguration, this.userManager, scoreboardService)
        );
        this.nameTagUpdateTask = this.individualNameTagManager.map(manager -> Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> manager.updatePlayers(false),
                100,
                scoreboardConfig.nametag.updateRate.getSeconds() * 20L
        ));

        this.dummyManager = Option.when(
                scoreboardConfig.dummy.enabled,
                () -> new DummyManager(this.pluginConfiguration, this.userManager, scoreboardService)
        );
        this.dummyUpdateTask = this.dummyManager.map(manager -> Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> manager.updatePlayers(false),
                100,
                scoreboardConfig.dummy.updateRate.getSeconds() * 20L
        ));

        this.scoreboardQueueUpdateTask = Option.when(
                this.individualNameTagManager.isPresent() || this.dummyManager.isPresent(),
                () -> Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        () -> {
                            for (int i = 0; i < scoreboardConfig.queueConfiguration.maxUpdatesInTick; i++) {
                                boolean nameTagUpdated = this.individualNameTagManager.is(IndividualNameTagManager::popAndUpdate);
                                boolean dummyUpdated = this.dummyManager.is(DummyManager::popAndUpdate);
                                if (!nameTagUpdated && !dummyUpdated) {
                                    break;
                                }
                            }
                        },
                        100,
                        scoreboardConfig.queueConfiguration.updateRate
                )
        );
    }

    public static FunnyGuilds getInstance() {
        return plugin;
    }

    public static FunnyGuildsLogger getPluginLogger() {
        return logger;
    }

    private static NmsAccessor prepareNmsAccessor() throws IllegalStateException {
        return NmsAccessor.instance();
    }

}
