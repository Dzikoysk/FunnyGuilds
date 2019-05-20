package net.dzikoysk.funnyguilds.command;

import java.io.IOException;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsVersion;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.commons.ConfigHelper;
import net.dzikoysk.funnyguilds.util.telemetry.FunnyTelemetry;
import net.dzikoysk.funnyguilds.util.telemetry.FunnybinResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcFunnyGuilds implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0 ? args[0].toLowerCase() : "";

        switch (parameter) {
            case "reload":
            case "rl":
                reload(sender);
                break;
            case "check":
            case "update":
                FunnyGuildsVersion.isNewAvailable(sender, true);
                break;
            case "save-all":
                saveAll(sender);
                break;
            case "funnybin":
                postConfig(sender);
                break;
            default:
                sender.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getInstance().getFullVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "FunnyGuilds Team");
                break;
        }

    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("funnyguilds.reload")) {
            sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
            return;
        }

        long startTime = System.currentTimeMillis();

        Thread thread = new Thread(() -> {
            FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
            funnyGuilds.reloadPluginConfiguration();
            funnyGuilds.reloadMessageConfiguration();
            funnyGuilds.getDataPersistenceHandler().reloadHandler();
            funnyGuilds.getDynamicListenerManager().reloadAll();

            if (FunnyGuilds.getInstance().getPluginConfiguration().playerListEnable) {
                PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
                AbstractTablist.wipeCache();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
                }
            }

            long endTime = System.currentTimeMillis();
            String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

            sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
        });

        sender.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
        thread.start();
    }

    private void saveAll(CommandSender sender) {
        if (!sender.hasPermission("funnyguilds.admin")) {
            sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Zapisywanie...");
        long l = System.currentTimeMillis();

        DataModel dataModel = FunnyGuilds.getInstance().getDataModel();

        try {
            dataModel.save(false);
            FunnyGuilds.getInstance().getInvitationPersistenceHandler().saveInvitations();
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("An error occurred while saving plugin data!", ex);
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA + (System.currentTimeMillis() - l) / 1000.0F + "s" + ChatColor.GRAY + ")!");
    }

    private void postConfig(CommandSender sender) {
        PluginConfiguration config = ConfigHelper.loadConfig(FunnyGuilds.getInstance().getPluginConfigurationFile(), PluginConfiguration.class);
        config.mysql.hostname = "<CUT>";
        config.mysql.database = "<CUT>";
        config.mysql.user = "<CUT>";
        config.mysql.password = "<CUT>";

        sender.sendMessage(ChatColor.GRAY + "Config jest wysylany...");

        sender.getServer().getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
            try {
                FunnybinResponse funnybinResponse = FunnyTelemetry.postToFunnybin(ConfigHelper.configToString(config));
                sender.sendMessage(ChatColor.GREEN + "Config wyslany. Link: " + ChatColor.AQUA + funnybinResponse.getShortUrl());
            }
            catch (IOException e) {
                FunnyGuilds.getInstance().getPluginLogger().error("Failed to send config", e);
                sender.sendMessage(ChatColor.DARK_RED + "Wystąpił błąd podczas wysyłania configu. ");
            }
        });
    }
}
