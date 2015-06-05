package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class DatabaseGuild {
	
	private final Guild guild;
	
	public DatabaseGuild(Guild guild){
		this.guild = guild;
	}
	
	public void save(Database db) {
		String update = getInsert();
		if(update != null) for(String query : update.split(";")){
			db.executeUpdate(query);
		}
	}
	
	public void delete() {
		if(guild == null) return;
		if(guild.getUUID() != null){
			Database db = Database.getInstance();
			db.openConnection();
			StringBuilder update = new StringBuilder();
			update.append("DELETE FROM guilds WHERE uuid='");
			update.append(guild.getUUID().toString());
			update.append("'");
			db.executeUpdate(update.toString());
			db.closeConnection();
		}else if(guild.getName() != null){
			Database db = Database.getInstance();
			db.openConnection();
			StringBuilder update = new StringBuilder();
			update.append("DELETE FROM guilds WHERE name='");
			update.append(guild.getName());
			update.append("'");
			db.executeUpdate(update.toString());
			db.closeConnection();
		}
	}
	
	public void updatePoints(){
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("UPDATE guilds SET points=");
		update.append(guild.getRank().getPoints());
		update.append(" WHERE uuid='");
		update.append(guild.getUUID().toString());
		update.append("'");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}

	public String getInsert(){
		StringBuilder sb = new StringBuilder();
		String members = StringUtils.toString(UserUtils.getNames(guild.getMembers()), false);
		String regions = StringUtils.toString(guild.getRegions(), false);
		String allies = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
		String enemies = StringUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);

		sb.append("INSERT INTO guilds (");
		sb.append("uuid, name, tag, owner, home, region, members, regions, allies, ");
		sb.append("enemies, points, born, validity, attacked, ban, lives, pvp");
		sb.append(") VALUES ('%uuid%','%name%','%tag%','%owner%','%home%','%region%',");
		sb.append("'%members%','%regions%','%allies%','%enemies%',%points%,%born%,");
		sb.append("%validity%,%attacked%,%ban%,%lives%,%pvp%) ON DUPLICATE KEY UPDATE ");
		sb.append("uuid='%uuid%',name='%name%',tag='%tag%',owner='%owner%',home='%home%',");
		sb.append("region='%region%',members='%members%',regions='%regions%',allies='%allies%',");
		sb.append("enemies='%enemies%',points=%points%,born=%born%,validity=%validity%,");
		sb.append("attacked=%attacked%,ban=%ban%,lives=%lives%,pvp=%pvp%");
		if(guild.getDeputy() != null) {
			sb.append("; UPDATE guilds SET deputy='");
			sb.append(guild.getDeputy().getName());
			sb.append("' WHERE uuid='");
			sb.append(guild.getUUID().toString());
			sb.append("'");
		} else {
			sb.append("; UPDATE guilds SET deputy=NULL WHERE uuid='");
			sb.append(guild.getUUID().toString());
			sb.append("'");
		}
		String is = sb.toString();
		is = StringUtils.replace(is, "%uuid%", guild.getUUID().toString());
		is = StringUtils.replace(is, "%name%", guild.getName());
		is = StringUtils.replace(is, "%tag%", guild.getTag());
		is = StringUtils.replace(is, "%owner%", guild.getOwner().getName());
		is = StringUtils.replace(is, "%home%", Parser.toString(guild.getHome()));
		is = StringUtils.replace(is, "%region%", guild.getRegion());
		is = StringUtils.replace(is, "%members%", members);
		is = StringUtils.replace(is, "%regions%", regions);
		is = StringUtils.replace(is, "%allies%", allies);
		is = StringUtils.replace(is, "%enemies%", enemies);
		is = StringUtils.replace(is, "%points%", Integer.toString(guild.getRank().getPoints()));
		is = StringUtils.replace(is, "%born%", Long.toString(guild.getBorn()));
		is = StringUtils.replace(is, "%validity%", Long.toString(guild.getValidity()));
		is = StringUtils.replace(is, "%attacked%", Long.toString(guild.getAttacked()));
		is = StringUtils.replace(is, "%ban%", Long.toString(guild.getBan()));
		is = StringUtils.replace(is, "%lives%", Integer.toString(guild.getLives()));
		is = StringUtils.replace(is, "%pvp%", Boolean.toString(guild.getPvP()));
		return is;
	}
	
	public static Guild deserialize(ResultSet rs) {
		if(rs == null) return null;
		try {
			String id = rs.getString("uuid");
			String name = rs.getString("name");
			String tag = rs.getString("tag");
			String os = rs.getString("owner");
			String dp = rs.getString("deputy");
			String home = rs.getString("home");
			String region = rs.getString("region");
			String m = rs.getString("members");
			String rgs = rs.getString("regions");
			String als = rs.getString("allies");
			String ens = rs.getString("enemies");
			boolean pvp = rs.getBoolean("pvp");
			long born = rs.getLong("born");
			long validity = rs.getLong("validity");
			long attacked = rs.getLong("attacked");
			long ban = rs.getLong("ban");
			int lives = rs.getInt("lives");
			
			if(name == null || tag == null || os == null){
				FunnyGuilds.error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
				return null;
			}
			
			UUID uuid = UUID.randomUUID();
			if(id != null) uuid = UUID.fromString(id);
			
			User owner = User.get(os);
			User deputy = null;
			if(dp != null) deputy = User.get(dp);
			List<User> members = new ArrayList<>();
			if(m != null && !m.equals("")) members = UserUtils.getUsers(StringUtils.fromString(m));
			List<String> regions = StringUtils.fromString(rgs);
			List<Guild> allies = new ArrayList<>();
			if(als != null && !als.equals("")) allies = GuildUtils.getGuilds(StringUtils.fromString(als));
			List<Guild> enemies = new ArrayList<>();
			if(ens != null && !ens.equals("")) enemies = GuildUtils.getGuilds(StringUtils.fromString(ens));
	
			if(born == 0) born = System.currentTimeMillis(); 
			if(validity == 0) validity = System.currentTimeMillis() + Settings.getInstance().validityStart; 
			if(lives == 0) lives = Settings.getInstance().warLives;
			
			Object[] values = new Object[17];
			values[0] = uuid;
			values[1] = name;
			values[2] = tag;
			values[3] = owner;
			values[4] = Parser.parseLocation(home);
			values[5] = region;
			values[6] = members;
			values[7] = regions;
			values[8] = allies;
			values[9] = enemies;
			values[10] = born;
			values[11] = validity;
			values[12] = attacked;
			values[13] = lives;
			values[14] = ban;
			values[15] = deputy;
			values[16] = pvp;
			return DeserializationUtils.deserializeGuild(values);
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return null;
	}

}
