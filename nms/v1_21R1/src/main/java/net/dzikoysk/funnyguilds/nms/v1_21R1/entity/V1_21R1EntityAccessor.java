package net.dzikoysk.funnyguilds.nms.v1_21R1.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.v1_20R5.entity.V1_20R5EntityAccessor;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;

public class V1_21R1EntityAccessor extends V1_20R5EntityAccessor {

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

}
