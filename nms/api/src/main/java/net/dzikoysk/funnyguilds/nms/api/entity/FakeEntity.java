package net.dzikoysk.funnyguilds.nms.api.entity;

import org.bukkit.Location;

public record FakeEntity(int id, Location location, Object spawnPacket) {

    public int chunkX() {
        return this.location.getBlockX() >> 4;
    }

    public int chunkZ() {
        return this.location.getBlockZ() >> 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        FakeEntity that = (FakeEntity) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
