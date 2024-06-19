package net.dzikoysk.funnyguilds.nms.v1_20R5;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_20R5.entity.V1_20R5EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_20R5.packet.V1_20_5PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_20R5.playerlist.V1_20R5PlayerList;
import net.dzikoysk.funnyguilds.nms.v1_20R5.statistics.V1_20R5StatisticsAccessor;

public class V1_20R5NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_20_5PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_20R5PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_20R5StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_20R5EntityAccessor();
    }

}
