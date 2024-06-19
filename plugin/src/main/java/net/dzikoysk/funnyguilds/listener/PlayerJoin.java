package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.user.BukkitUserProfile;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

public class PlayerJoin extends AbstractFunnyListener {

    @Inject
    private TablistConfiguration tablistConfig;
    @Inject
    private NmsAccessor nmsAccessor;
    @Inject
    public GuildEntityHelper guildEntityHelper;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = this.userManager.findByPlayer(player)
                .peek(foundUser -> foundUser.getProfile().refresh())
                .orElseGet(() -> {
                    UserProfile profile = new BukkitUserProfile(player.getUniqueId(), this.funnyServer);
                    return this.userManager.create(player.getUniqueId(), player.getName(), profile);
                });

        String playerName = player.getName();
        if (!user.getName().equals(playerName)) {
            this.userManager.updateUsername(user, playerName);
        }

        UserCache cache = user.getCache();

        if (this.tablistConfig.enabled) {
            IndividualPlayerList individualPlayerList = new IndividualPlayerList(
                    user,
                    this.nmsAccessor.getPlayerListAccessor(),
                    this.funnyServer,
                    this.tablistConfig.cells,
                    this.tablistConfig.header, this.tablistConfig.footer,
                    this.tablistConfig.animated, this.tablistConfig.pages,
                    this.tablistConfig.heads.textures,
                    this.tablistConfig.cellsPing,
                    this.tablistConfig.fillCells
            );

            individualPlayerList.send();
            cache.setPlayerList(individualPlayerList);
        }

        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user, true))
                .peek(this.plugin::scheduleFunnyTasks);
        this.plugin.getDummyManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user, true))
                .peek(this.plugin::scheduleFunnyTasks);

        FunnyGuildsInboundChannelHandler inboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallInboundChannelHandler(player);
        inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(this.plugin, user));

        FunnyGuildsOutboundChannelHandler outboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallOutboundChannelHandler(player);
        outboundChannelHandler.getPacketSuppliersRegistry().setOwner(player);
        outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.guildEntityHelper));

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getVersion().isNewAvailable(player, false);
        }, 30L);
    }

}
