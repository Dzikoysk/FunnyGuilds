package net.dzikoysk.funnyguilds.nms.api.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import java.util.function.Supplier;

import static net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessorConstants.FUNNY_GUILDS_OUT_HANDLER_ID;

public class GenericOutboundChannelHandlerInstaller<T extends FunnyGuildsOutboundChannelHandler & ChannelHandler> {

    private final Supplier<T> channelHandlerSupplier;

    public GenericOutboundChannelHandlerInstaller(Supplier<T> channelHandlerSupplier) {
        this.channelHandlerSupplier = channelHandlerSupplier;
    }

    public FunnyGuildsOutboundChannelHandler installChannelHandlerOutPipeline(ChannelPipeline pipeline) {
        ChannelHandler oldChannelHandler = pipeline.get(FUNNY_GUILDS_OUT_HANDLER_ID);

        if (oldChannelHandler == null) {
            T newChannelHandler = this.channelHandlerSupplier.get();
            pipeline.addBefore("packet_handler", FUNNY_GUILDS_OUT_HANDLER_ID, newChannelHandler);

            return newChannelHandler;
        }

        if (oldChannelHandler instanceof FunnyGuildsOutboundChannelHandler) {
            return (FunnyGuildsOutboundChannelHandler) oldChannelHandler;
        }

        // this case handles /reload
        T newChannelHandler = this.channelHandlerSupplier.get();
        pipeline.replace(FUNNY_GUILDS_OUT_HANDLER_ID, FUNNY_GUILDS_OUT_HANDLER_ID, newChannelHandler);

        return newChannelHandler;
    }

}
