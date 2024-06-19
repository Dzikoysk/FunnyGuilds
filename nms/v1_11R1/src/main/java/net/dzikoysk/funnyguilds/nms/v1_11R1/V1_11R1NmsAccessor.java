package net.dzikoysk.funnyguilds.nms.v1_11R1;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.entity.V1_11R1EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.packet.V1_11R1PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.playerlist.V1_11R1PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.statistics.V1_11R1StatisticsAccessor;

public class V1_11R1NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_11R1PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return new V1_11R1PlayerListAccessor();
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_11R1StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_11R1EntityAccessor();
    }

}
