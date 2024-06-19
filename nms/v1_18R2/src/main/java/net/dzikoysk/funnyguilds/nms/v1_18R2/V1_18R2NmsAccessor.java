package net.dzikoysk.funnyguilds.nms.v1_18R2;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_18R2.entity.V1_18R2EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_18R2.packet.V1_18R2PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_18R2.playerlist.V1_18R2PlayerList;
import net.dzikoysk.funnyguilds.nms.v1_18R2.statistics.V1_18R2StatisticsAccessor;

public class V1_18R2NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_18R2PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_18R2PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_18R2StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_18R2EntityAccessor();
    }

}
