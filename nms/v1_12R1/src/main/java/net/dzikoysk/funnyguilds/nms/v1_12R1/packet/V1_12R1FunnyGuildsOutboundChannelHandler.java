package net.dzikoysk.funnyguilds.nms.v1_12R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;

public class V1_12R1FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {
    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    private static final Field CHUNK_X_FIELD;
    private static final Field CHUNK_Z_FIELD;

    static {
        try {
            CHUNK_X_FIELD = PacketPlayOutMapChunk.class.getDeclaredField("a");
            CHUNK_X_FIELD.setAccessible(true);
            CHUNK_Z_FIELD = PacketPlayOutMapChunk.class.getDeclaredField("b");
            CHUNK_Z_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException("Failed to initialise V1_12R1FunnyGuildsOutboundChannelHandler", ex);
        }
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof PacketPlayOutMapChunk) {
            PacketPlayOutMapChunk chunkPacket = (PacketPlayOutMapChunk) msg;

            int chunkX = (int) CHUNK_X_FIELD.get(chunkPacket);
            int chunkZ = (int) CHUNK_Z_FIELD.get(chunkPacket);

            for (FakeEntity fakeEntity : packetSuppliersRegistry.supplyFakeEntities(chunkX, chunkZ)) {
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
