package net.dzikoysk.funnyguilds.nms.v1_8R3;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.entity.V1_8R3EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.V1_8R3PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.playerlist.V1_8R3PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.statistics.V1_8R3StatisticsAccessor;

public class V1_8R3NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_8R3PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return new V1_8R3PlayerListAccessor();
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_8R3StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_8R3EntityAccessor();
    }

}
