package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final FunnyGuilds plugin;

    public PlayerJoin(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        User user = User.get(player);
        user.getScoreboard();

        IndependentThread.actions(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);
        IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, user);
        IndependentThread.actions(ActionType.RANK_UPDATE_USER, user);
        IndependentThread.action(ActionType.PLAYERLIST_SEND, user);

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            PacketExtension.registerPlayer(player);
            Region region = RegionUtils.getAt(player.getLocation());
            Version.check(player);
            if(region == null || region.getGuild() == null){
                return;
            }
            if (Settings.getConfig().createStringMaterial.equalsIgnoreCase("ender crystal")) {
                EntityUtil.spawn(region.getGuild(), player);
            }
        }, 30L);
    }
}
