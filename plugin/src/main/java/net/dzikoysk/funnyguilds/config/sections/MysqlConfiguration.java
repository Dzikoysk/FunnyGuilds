package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.annotation.Variable;
import net.dzikoysk.funnyguilds.config.ConfigSection;

public class MysqlConfiguration extends ConfigSection {

    @Variable("FG_MYSQL_HOSTNAME")
    public String hostname = "localhost";
    @Variable("FG_MYSQL_PORT")
    public int port = 3306;
    @Variable("FG_MYSQL_DATABASE")
    public String database = "db";
    @Variable("FG_MYSQL_USER")
    public String user = "root";
    @Variable("FG_MYSQL_PASSWORD")
    public String password = "passwd";

    @Variable("FG_MYSQL_POOL_SIZE")
    public int poolSize = 5;
    @Variable("FG_MYSQL_CONNECTION_TIMEOUT")
    public int connectionTimeout = 30000;
    @Variable("FG_MYSQL_USE_SSL")
    public boolean useSSL = true;
    @Variable("FG_MYSQL_CHARACTER_ENCODING")
    public String characterEncoding = "";

    @Variable("FG_MYSQL_USERS_TABLE_NAME")
    public String usersTableName = "users";
    @Variable("FG_MYSQL_GUILDS_TABLE_NAME")
    public String guildsTableName = "guilds";
    @Variable("FG_MYSQL_REGIONS_TABLE_NAME")
    public String regionsTableName = "regions";

}
