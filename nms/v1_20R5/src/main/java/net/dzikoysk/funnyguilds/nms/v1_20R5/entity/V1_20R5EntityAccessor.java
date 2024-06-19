package net.dzikoysk.funnyguilds.nms.v1_20R5.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class V1_20R5EntityAccessor implements EntityAccessor {

    @Override
    public FakeEntity createFakeEntity(EntityType entityType, Location location) {
        Preconditions.checkNotNull(entityType, "entity type can't be null!");
        Preconditions.checkNotNull(location, "location can't be null!");
        Preconditions.checkArgument(entityType.isSpawnable(), "entity type is not spawnable!");

        CraftWorld world = ((CraftWorld) location.getWorld());

        if (world == null) {
            throw new IllegalStateException("location's world is null!");
        }

        net.minecraft.world.entity.Entity entity = world.createEntity(location, entityType.getEntityClass(), true);
        Packet<?> spawnEntityPacket;

        spawnEntityPacket = new ClientboundAddEntityPacket(entity);

        return new FakeEntity(entity.getId(), location, spawnEntityPacket);
    }

    @Override
    public void spawnFakeEntityFor(FakeEntity entity, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.send((Packet<?>) entity.getSpawnPacket());
        }
    }

    @Override
    public void despawnFakeEntityFor(FakeEntity entity, Player... players) {
        ClientboundRemoveEntitiesPacket destroyEntityPacket = new ClientboundRemoveEntitiesPacket(entity.getId());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.send(destroyEntityPacket);
        }
    }

}
