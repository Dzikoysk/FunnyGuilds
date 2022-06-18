package net.dzikoysk.funnyguilds.data.database;

import java.sql.SQLException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLType;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class SQLDataModel implements DataModel {

    private final FunnyGuilds plugin;

    private final SQLTable usersTable;
    private final SQLTable guildsTable;
    private final SQLTable regionsTable;


    public SQLDataModel(FunnyGuilds plugin) {
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();

        this.plugin = plugin;
        this.usersTable = new SQLTable(pluginConfiguration.mysql.usersTableName);
        this.guildsTable = new SQLTable(pluginConfiguration.mysql.guildsTableName);
        this.regionsTable = new SQLTable(pluginConfiguration.mysql.regionsTableName);

        this.prepareTables();
    }

    public void prepareTables() {
        this.usersTable.add("uuid", SQLType.VARCHAR, 36, true);
        this.usersTable.add("name", SQLType.TEXT, true);
        this.usersTable.add("points", SQLType.INT, true);
        this.usersTable.add("kills", SQLType.INT, true);
        this.usersTable.add("deaths", SQLType.INT, true);
        this.usersTable.add("assists", SQLType.INT, true);
        this.usersTable.add("logouts", SQLType.INT, true);
        this.usersTable.add("ban", SQLType.BIGINT);
        this.usersTable.add("reason", SQLType.TEXT);
        this.usersTable.setPrimaryKey("uuid");

        this.guildsTable.add("uuid", SQLType.VARCHAR, 100, true);
        this.guildsTable.add("name", SQLType.TEXT, true);
        this.guildsTable.add("tag", SQLType.TEXT, true);
        this.guildsTable.add("owner", SQLType.TEXT, true);
        this.guildsTable.add("home", SQLType.TEXT, true);
        this.guildsTable.add("region", SQLType.TEXT, true);
        this.guildsTable.add("regions", SQLType.TEXT, true);
        this.guildsTable.add("members", SQLType.TEXT, true);
        this.guildsTable.add("points", SQLType.INT, true);
        this.guildsTable.add("lives", SQLType.INT, true);
        this.guildsTable.add("ban", SQLType.BIGINT, true);
        this.guildsTable.add("born", SQLType.BIGINT, true);
        this.guildsTable.add("validity", SQLType.BIGINT, true);
        this.guildsTable.add("pvp", SQLType.BOOLEAN, true);
        this.guildsTable.add("attacked", SQLType.BIGINT); //TODO: [FG 5.0] attacked -> protection
        this.guildsTable.add("allies", SQLType.TEXT);
        this.guildsTable.add("enemies", SQLType.TEXT);
        this.guildsTable.add("info", SQLType.TEXT);
        this.guildsTable.add("deputy", SQLType.TEXT);
        this.guildsTable.setPrimaryKey("uuid");

        this.regionsTable.add("name", SQLType.VARCHAR, 100, true);
        this.regionsTable.add("center", SQLType.TEXT, true);
        this.regionsTable.add("size", SQLType.INT, true);
        this.regionsTable.add("enlarge", SQLType.INT, true);
        this.regionsTable.setPrimaryKey("name");
    }

    public void load() throws SQLException {
        createTableIfNotExists(this.usersTable);
        createTableIfNotExists(this.regionsTable);
        createTableIfNotExists(this.guildsTable);

        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        ConcurrencyManager concurrencyManager = this.plugin.getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest(this.plugin.getIndividualPrefixManager()));
    }

    public void loadUsers() {
        SQLBasicUtils.getSelectAll(this.usersTable).executeQuery(result -> {
            while (result.next()) {
                String userName = result.getString("name");

                if (!FunnyValidator.validateUsername(userName)) {
                    FunnyGuilds.getPluginLogger().warning("Skipping loading of user '" + userName + "' - name is invalid");
                    continue;
                }

                DatabaseUserSerializer.deserialize(result).peek(User::wasChanged);
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded users: " + this.plugin.getUserManager().countUsers());
    }

    public void loadGuilds() {
        GuildManager guildManager = this.plugin.getGuildManager();

        SQLBasicUtils.getSelectAll(this.guildsTable).executeQuery(resultAll -> {
            while (resultAll.next()) {
                DatabaseGuildSerializer.deserialize(resultAll).peek(Guild::wasChanged);
            }
        });

        SQLBasicUtils.getSelect(this.guildsTable, "tag", "allies", "enemies").executeQuery(result -> {
            while (result.next()) {
                Option<Guild> guildOption = guildManager.findByTag(result.getString("tag"));
                if (guildOption.isEmpty()) {
                    continue;
                }

                Guild guild = guildOption.get();
                String alliesList = result.getString("allies");
                String enemiesList = result.getString("enemies");

                if (alliesList != null && !alliesList.isEmpty()) {
                    guild.setAllies(guildManager.findByNames(FunnyStringUtils.fromString(alliesList)));
                }

                if (enemiesList != null && !enemiesList.isEmpty()) {
                    guild.setEnemies(guildManager.findByNames(FunnyStringUtils.fromString(enemiesList)));
                }
            }
        });

        PandaStream.of(guildManager.getGuilds())
                .filter(guild -> guild.getOwner() == null)
                .forEach(guild -> guildManager.deleteGuild(FunnyGuilds.getInstance(), guild));

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guildManager.countGuilds());
    }

    public void loadRegions() {
        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        RegionManager regionManager = this.plugin.getRegionManager();

        SQLBasicUtils.getSelectAll(this.regionsTable).executeQuery(result -> {
            while (result.next()) {
                DatabaseRegionSerializer.deserialize(result).peek(region -> {
                    region.wasChanged();
                    regionManager.addRegion(region);
                });
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + regionManager.countRegions());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        PandaStream.of(this.plugin.getUserManager().getUsers())
                .filter(user -> !ignoreNotChanged || user.wasChanged())
                .forEach(DatabaseUserSerializer::serialize);

        PandaStream.of(this.plugin.getGuildManager().getGuilds())
                .filter(guild -> !ignoreNotChanged || guild.wasChanged())
                .forEach(DatabaseGuildSerializer::serialize);

        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            return;
        }

        PandaStream.of(this.plugin.getRegionManager().getRegions())
                .filter(region -> !ignoreNotChanged || region.wasChanged())
                .forEach(DatabaseRegionSerializer::serialize);
    }

    public SQLTable getUsersTable() {
        return this.usersTable;
    }

    public SQLTable getGuildsTable() {
        return this.guildsTable;
    }

    public SQLTable getRegionsTable() {
        return this.regionsTable;
    }

    private static void createTableIfNotExists(SQLTable table) {
        SQLBasicUtils.getCreate(table).executeUpdate();
        table.getSqlElements().forEach(sqlElement -> SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true));
    }

}
