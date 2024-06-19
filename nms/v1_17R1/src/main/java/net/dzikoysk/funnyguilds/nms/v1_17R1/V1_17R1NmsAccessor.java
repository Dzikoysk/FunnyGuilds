package net.dzikoysk.funnyguilds.nms.v1_17R1;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_17R1.entity.V1_17R1EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_17R1.packet.V1_17R1PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_17R1.playerlist.V1_17R1PlayerList;
import net.dzikoysk.funnyguilds.nms.v1_17R1.statistics.V1_17R1StatisticsAccessor;

public class V1_17R1NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_17R1PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_17R1PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_17R1StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_17R1EntityAccessor();
    }

}
