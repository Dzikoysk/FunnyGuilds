package net.dzikoysk.funnyguilds.nms.api;

import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;

public interface NmsAccessor {

    static NmsAccessor instance() {
        return NmsAccessorHolder.INSTANCE;
    }

    PacketAccessor getPacketAccessor();

    PlayerListAccessor getPlayerListAccessor();

    StatisticsAccessor getStatisticsAccessor();

    EntityAccessor getEntityAccessor();

}
