package net.dzikoysk.funnyguilds.nms.impl;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.impl.entity.EntityAccessorImpl;
import net.dzikoysk.funnyguilds.nms.impl.packet.PacketAccessorImpl;
import net.dzikoysk.funnyguilds.nms.impl.playerlist.PlayerListImpl;

public class NmsAccessorImpl implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new PacketAccessorImpl();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return PlayerListImpl::new;
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new EntityAccessorImpl();
    }

}
