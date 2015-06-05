package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;

import org.bukkit.Location;

public class DatabaseRegion {

	private final Region region;
	
	public DatabaseRegion(Region region){
		this.region = region;
	}
	
	public void save(Database db) {
		String update = getInsert();
		if(update != null) db.executeUpdate(update);
	}
	
	public void delete() {
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("DELETE FROM regions WHERE name='");
		update.append(region.getName());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}
	
	public String getInsert(){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO regions (name, center, size, enlarge) VALUES (");
		sb.append("'" + region.getName() + "',");
		sb.append("'" + Parser.toString(region.getCenter()) + "',");
		sb.append("'" + region.getSize() + "',");
		sb.append("'" + region.getEnlarge() + "'");
		sb.append(") ON DUPLICATE KEY UPDATE ");
		sb.append("center='" + Parser.toString(region.getCenter()) + "',");
		sb.append("size=" + region.getSize() + ",");
		sb.append("enlarge=" + region.getEnlarge() + ";");
		return sb.toString();
	}
	
	public static Region deserialize(ResultSet rs) {
		if(rs == null) return null;
		try {
			String name = rs.getString("name");
			String center = rs.getString("center");
			int size = rs.getInt("size");
			int enlarge = rs.getInt("enlarge");
			Location loc = Parser.parseLocation(center);
			
			if(name == null){
				FunnyGuilds.error("Cannot deserialize region! Caused by: name == null");
				return null;
			}else if(loc == null){
				FunnyGuilds.error("Cannot deserialize region (" + name + ") ! Caused by: loc == null");
				return null;
			}
			
			Object[] values = new Object[4];
			values[0] = name;
			values[1] = loc;
			values[2] = size;
			values[3] = enlarge;
			return DeserializationUtils.deserializeRegion(values);
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return null;
	}
}
