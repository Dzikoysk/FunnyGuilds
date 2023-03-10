package net.dzikoysk.funnyguilds.nms;

import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import org.bukkit.plugin.PluginDescriptionFile;

public final class DescriptionChanger {

    private final PluginDescriptionFile descriptionFile;

    public DescriptionChanger(PluginDescriptionFile descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public void rename(String pluginName) {
        if (FunnyStringUtils.isEmpty(pluginName)) {
            return;
        }

        try {
            Field field = this.descriptionFile.getClass().getDeclaredField("name");
            field.set(this.descriptionFile, pluginName);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not change description file", exception);
        }
    }

}
