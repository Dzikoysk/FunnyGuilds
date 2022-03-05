package net.dzikoysk.funnyguilds.nms.v1_13R2.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.GenericInboundChannelHandlerInstaller;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_13R2PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller channelHandlerInstaller = new GenericInboundChannelHandlerInstaller(V1_13R2FunnyGuildsChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.channelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }
}
