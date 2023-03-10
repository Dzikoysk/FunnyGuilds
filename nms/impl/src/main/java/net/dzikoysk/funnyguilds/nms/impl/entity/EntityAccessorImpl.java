package net.dzikoysk.funnyguilds.nms.impl.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityAccessorImpl implements EntityAccessor {

    @Override
    public FakeEntity createFakeEntity(EntityType entityType, Location location) {
        Preconditions.checkNotNull(entityType, "entity type can't be null!");
        Preconditions.checkNotNull(location, "location can't be null!");
        Preconditions.checkArgument(entityType.isSpawnable(), "entity type is not spawnable!");

        CraftWorld world = ((CraftWorld) location.getWorld());

        if (world == null) {
            throw new IllegalStateException("location's world is null!");
        }

        Entity entity = world.createEntity(location, entityType.getEntityClass(), true);
        ClientboundAddEntityPacket spawnEntityPacket = new ClientboundAddEntityPacket(
                entity.getId(),
                entity.getUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getPitch(),
                location.getYaw(),
                entity.getType(),
                0,
                entity.getDeltaMovement(),
                entity.getYHeadRot()
        );

        return new FakeEntity(entity.getId(), location, spawnEntityPacket);
    }

    @Override
    public void spawnFakeEntityFor(FakeEntity entity, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.send((Packet<?>) entity.spawnPacket());
        }
    }

    @Override
    public void despawnFakeEntityFor(FakeEntity entity, Player... players) {
        ClientboundRemoveEntitiesPacket destroyEntityPacket = new ClientboundRemoveEntitiesPacket(entity.id());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.send(destroyEntityPacket);
        }
    }

}
