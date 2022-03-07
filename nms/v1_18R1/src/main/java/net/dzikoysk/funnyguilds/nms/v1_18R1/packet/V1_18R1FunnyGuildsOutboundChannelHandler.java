package net.dzikoysk.funnyguilds.nms.v1_18R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;

public class V1_18R1FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {

    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    private static final Field CHUNK_X_FIELD;
    private static final Field CHUNK_Z_FIELD;

    static {
        try {
            CHUNK_X_FIELD = ClientboundLevelChunkWithLightPacket.class.getDeclaredField("a");
            CHUNK_X_FIELD.setAccessible(true);
            CHUNK_Z_FIELD = ClientboundLevelChunkWithLightPacket.class.getDeclaredField("b");
            CHUNK_Z_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException("Failed to initialise V1_18R1FunnyGuildsOutboundChannelHandler", ex);
        }
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof ClientboundLevelChunkWithLightPacket) {
            ClientboundLevelChunkWithLightPacket chunkPacket = (ClientboundLevelChunkWithLightPacket) msg;

            int xChunk = (int) CHUNK_X_FIELD.get(chunkPacket);
            int zChunk = (int) CHUNK_Z_FIELD.get(chunkPacket);

            for (FakeEntity fakeEntity : packetSuppliersRegistry.supplyFakeEntities(new int[] {xChunk, zChunk})) {
                ctx.write(fakeEntity.getSpawnPacket());
            }
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public PacketSuppliersRegistry getPacketSuppliersRegistry() {
        return packetSuppliersRegistry;
    }

}
