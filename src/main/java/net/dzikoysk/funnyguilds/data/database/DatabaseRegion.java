package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.data.database.element.SQLBuilderStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLUtils;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.sql.ResultSet;

public class DatabaseRegion {

    private final Region region;

    public DatabaseRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(ResultSet rs) {
        if (rs == null) {
            return null;
        }
        
        try {
            String name = rs.getString("name");
            String center = rs.getString("center");
            int size = rs.getInt("size");
            int enlarge = rs.getInt("enlarge");
            Location location = LocationUtils.parseLocation(center);

            if (name == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region! Caused by: name == null");
                return null;
            } else if (location == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region (" + name + ") ! Caused by: loc == null");
                return null;
            }

            Object[] values = new Object[4];
            
            values[0] = name;
            values[1] = location;
            values[2] = size;
            values[3] = enlarge;
            
            return DeserializationUtils.deserializeRegion(values);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not deserialize region", ex);
        }
        
        return null;
    }

    public void save() {
        SQLBuilderStatement builderPS = SQLUtils.getBuilderInsert(SQLDataModel.tabRegions);

        builderPS.set("name", region.getName());
        builderPS.set("center", LocationUtils.toString(region.getCenter()));
        builderPS.set("size", region.getSize());
        builderPS.set("enlarge", region.getEnlarge());
        builderPS.executeUpdate();
    }

    public void delete() {
        SQLBuilderStatement builderPS = SQLUtils.getBuilderDelete(SQLDataModel.tabRegions);

        builderPS.set("name", region.getName());
        builderPS.executeUpdate();
    }
}
