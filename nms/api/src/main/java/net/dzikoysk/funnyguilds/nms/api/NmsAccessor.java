package net.dzikoysk.funnyguilds.nms.api;

import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface NmsAccessor {

    PacketAccessor getPacketAccessor();

    PlayerListAccessor getPlayerListAccessor();

    EntityAccessor getEntityAccessor();

}
