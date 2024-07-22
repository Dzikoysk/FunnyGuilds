package net.dzikoysk.funnyguilds.legacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Deprecated
//https://github.com/PaperMC/paper-trail/blob/master/src/main/java/io/papermc/papertrail/RequiresPaperPlugins.java
public class FunnyGuildsLegacyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.printInformation();
        this.disable();
    }

    private void printInformation() {
        final List<String> lines = new ArrayList<>(Arrays.asList("", LegacyPluginUtil.EQUALS_LINE));
        lines.addAll(LegacyPluginUtil.PAPER ? this.outdatedPaper() : this.requiresPaper());
        lines.add(LegacyPluginUtil.EQUALS_LINE);
        this.getLogger().log(Level.SEVERE, String.join("\n", lines),
                new UnsupportedPlatformException("Unsupported platform"));
    }

    private void disable() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    private List<String> outdatedPaper() {
        String pluginName = this.getDescription().getName();
        return Arrays.asList(
                " " + pluginName + " requires a latest version of Paper",
                "",
                " Download latest Paper: https://papermc.io"
        );
    }

    private List<String> requiresPaper() {
        String pluginName = this.getDescription().getName();
        return Arrays.asList(
                " " + pluginName + " is a Paper plugin, meaning it only supports",
                " Paper and derivatives, not Spigot or CraftBukkit.",
                "",
                " Download latest Paper: https://papermc.io"
        );
    }

}
