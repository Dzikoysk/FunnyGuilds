package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatGuildSerializer;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatUserSerializer;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseFixAlliesAsyncTask;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Option;
import panda.std.Result;
import panda.std.stream.PandaStream;

public class FlatDataModel implements DataModel {

    private final FunnyGuilds plugin;
    private final PluginConfiguration pluginConfiguration;

    private final File usersFolderFile;
    private final File guildsFolderFile;
    private final File regionsFolderFile;

    public FlatDataModel(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();

        File dataFolder = plugin.getPluginDataFolder();
        this.usersFolderFile = new File(dataFolder, "users");
        this.guildsFolderFile = new File(dataFolder, "guilds");
        this.regionsFolderFile = new File(dataFolder, "regions");
    }

    private Option<File> loadCustomFile(EntityType type, String name) {
        File fileFolder;

        switch (type) {
            case USER:
                fileFolder = this.usersFolderFile;
                break;
            case GUILD:
                fileFolder = this.guildsFolderFile;
                break;
            case REGION:
                fileFolder = this.regionsFolderFile;
                break;
            default:
                fileFolder = null;
        }

        if (fileFolder == null) {
            return Option.none();
        }

        Result<File, String> createResult = FunnyIOUtils.createFile(new File(fileFolder, name + ".yml"), false);
        if (createResult.isErr()) {
            FunnyGuilds.getPluginLogger().error(createResult.getError());
            return Option.none();
        }

        return Option.of(createResult.get());
    }

    public Option<File> getUserFile(User user) {
        return this.loadCustomFile(EntityType.USER, user.getUUID().toString());
    }

    public Option<File> getGuildFile(Guild guild) {
        return this.loadCustomFile(EntityType.GUILD, guild.getName());
    }

    public Option<File> getRegionFile(Region region) {
        return this.loadCustomFile(EntityType.REGION, region.getName());
    }

    @Override
    public void load() {
        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        this.validateLoadedData();
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.saveUsers(ignoreNotChanged);
        this.saveRegions(ignoreNotChanged);
        this.saveGuilds(ignoreNotChanged);
    }

    private void loadUsers() {
        UserManager userManager = this.plugin.getUserManager();
        userManager.clearUsers();

        File[] userFiles = this.usersFolderFile.listFiles();
        if (userFiles == null || userFiles.length == 0) {
            FunnyGuilds.getPluginLogger().info("No users to load");
            return;
        }

        AtomicInteger deserializationErrors = new AtomicInteger();
        PandaStream.of(userFiles)
                .filter(file -> file.length() != 0)
                .mapOpt(file -> UserUtils.checkUserFile(this.pluginConfiguration, file))
                .forEach(file -> FlatUserSerializer.deserialize(file)
                        .onEmpty(deserializationErrors::incrementAndGet)
                );

        if (deserializationErrors.get() > 0) {
            FunnyGuilds.getPluginLogger().error("Users load errors " + deserializationErrors.get());
        }

        FunnyGuilds.getPluginLogger().info("Loaded users: " + this.plugin.getUserManager().countUsers());
    }

    private void saveUsers(boolean ignoreNotChanged) {
        Set<User> users = this.plugin.getUserManager().getUsers();
        if (users.isEmpty()) {
            return;
        }

        AtomicInteger incorrectUsersCount = new AtomicInteger();
        long serializationErrors = PandaStream.of(users)
                .filter(user -> checkUser(user, incorrectUsersCount))
                .filter(user -> !ignoreNotChanged || user.wasChanged())
                .filterNot(FlatUserSerializer::serialize)
                .count();

        long errors = serializationErrors + incorrectUsersCount.get();
        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Users save errors " + errors);
        }
    }

    private void loadGuilds() {
        GuildManager guildManager = this.plugin.getGuildManager();
        guildManager.clearGuilds();

        File[] guildFiles = this.guildsFolderFile.listFiles();
        if (guildFiles == null || guildFiles.length == 0) {
            FunnyGuilds.getPluginLogger().info("No guilds to load");
            return;
        }

        AtomicInteger incorrectGuildsCount = new AtomicInteger();
        AtomicInteger ownerlessGuilds = new AtomicInteger();

        PandaStream.of(guildFiles)
                .mapOpt(guildFile -> FlatGuildSerializer.deserialize(guildFile)
                        .onEmpty(incorrectGuildsCount::incrementAndGet))
                .filter(guild -> guild.getOwner() == null)
                .forEach(guild -> {
                    FunnyGuilds.getPluginLogger().error("Guild " + guild.getTag() + " has no owner!");
                    ownerlessGuilds.incrementAndGet();
                });

        long errors = incorrectGuildsCount.get() + ownerlessGuilds.get();
        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guild load errors " + errors);
        }

        this.plugin.scheduleFunnyTasks(new DatabaseFixAlliesAsyncTask(guildManager));
        this.plugin.getIndividualNameTagManager().map(ScoreboardGlobalUpdateSyncTask::new).peek(this.plugin::scheduleFunnyTasks);

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guildManager.countGuilds());
    }

    private void saveGuilds(boolean ignoreNotChanged) {
        Set<Guild> guilds = this.plugin.getGuildManager().getGuilds();
        if (guilds.isEmpty()) {
            return;
        }

        long errors = PandaStream.of(guilds)
                .filter(guild -> !ignoreNotChanged || guild.wasChanged())
                .filterNot(FlatGuildSerializer::serialize)
                .count();

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Guilds save errors: " + errors);
        }
    }

    private void loadRegions() {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        RegionManager regionManager = this.plugin.getRegionManager();
        regionManager.clearRegions();

        File[] regionFiles = this.regionsFolderFile.listFiles();
        if (regionFiles == null || regionFiles.length == 0) {
            FunnyGuilds.getPluginLogger().info("No regions to load");
            return;
        }

        AtomicInteger correctlyLoaded = new AtomicInteger();
        PandaStream.of(regionFiles)
                .mapOpt(FlatRegionSerializer::deserialize)
                .forEach(region -> {
                    regionManager.addRegion(region);
                    correctlyLoaded.incrementAndGet();
                });

        long errors = regionFiles.length - correctlyLoaded.get();
        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Region load errors " + errors);
        }

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + regionManager.countRegions());
    }

    private void saveRegions(boolean ignoreNotChanged) {
        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            return;
        }

        Set<Region> regions = this.plugin.getRegionManager().getRegions();
        if (regions.isEmpty()) {
            return;
        }

        long errors = PandaStream.of(regions)
                .filter(region -> !ignoreNotChanged || region.wasChanged())
                .filterNot(FlatRegionSerializer::serialize)
                .count();

        if (errors > 0) {
            FunnyGuilds.getPluginLogger().error("Regions save errors: " + errors);
        }
    }

    private static boolean checkUser(User user, AtomicInteger errorCounter) {
        if (user.getUUID() == null || user.getName() == null) {
            errorCounter.incrementAndGet();
            return false;
        }

        return true;
    }

}
