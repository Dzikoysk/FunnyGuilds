package net.dzikoysk.funnyguilds.nms.heart;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;
import org.bukkit.World;
import panda.std.stream.PandaStream;

public class GuildEntitySupplier implements PacketSuppliers {

    private final GuildEntityHelper helper;

    public GuildEntitySupplier(GuildEntityHelper helper) {
        this.helper = helper;
    }

    @Override
    public Collection<FakeEntity> supplyFakeEntities(World world, int chunkX, int chunkZ) {
        return PandaStream.of(this.helper.getGuildEntities().values())
                .filter(entity -> entity.location().getWorld().equals(world))
                .filter(entity -> entity.chunkX() == chunkX)
                .filter(entity -> entity.chunkZ() == chunkZ)
                .toSet();
    }

}
