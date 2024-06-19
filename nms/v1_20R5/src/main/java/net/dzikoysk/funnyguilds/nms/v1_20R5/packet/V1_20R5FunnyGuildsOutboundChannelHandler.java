package net.dzikoysk.funnyguilds.nms.v1_20R5.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;

public class V1_20R5FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {
    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ClientboundLevelChunkWithLightPacket chunkPacket) {
            int chunkX = chunkPacket.getX();
            int chunkZ = chunkPacket.getZ();

            for (FakeEntity fakeEntity : this.packetSuppliersRegistry.supplyFakeEntities(chunkX, chunkZ)) {
                ctx.write(fakeEntity.getSpawnPacket());
            }
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public PacketSuppliersRegistry getPacketSuppliersRegistry() {
        return this.packetSuppliersRegistry;
    }
}
