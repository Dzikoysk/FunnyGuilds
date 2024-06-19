package net.dzikoysk.funnyguilds.nms.v1_20R5.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class V1_20R5FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ServerboundInteractPacket interactPacket) {
            int entityId = interactPacket.getEntityId();

            if (interactPacket.isAttack()) {
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else {
                InteractionHand interactionHand = getInteractionHand(interactPacket);

                if (interactionHand != null) {
                    boolean isMainHand = interactionHand == InteractionHand.MAIN_HAND;

                    this.packetCallbacksRegistry.handleRightClickEntity(entityId, isMainHand);
                }
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }

    private @Nullable InteractionHand getInteractionHand(ServerboundInteractPacket interactPacket) {
        InteractionHand[] interactHand = new InteractionHand[1];

        interactPacket.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand interactionHand) {
                // no-op
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 vec3) {
                // interact at
                interactHand[0] = interactionHand;
            }

            @Override
            public void onAttack() {
                // no-op
            }
        });

        return interactHand[0];
    }
}
