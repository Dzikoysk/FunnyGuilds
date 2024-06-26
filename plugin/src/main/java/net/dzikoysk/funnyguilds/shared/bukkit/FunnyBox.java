package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Objects;
import net.dzikoysk.funnyguilds.shared.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

//TODO: Investigate if we even need this clas or we can use just BoundingBox
public class FunnyBox {

    public static FunnyBox of(Block block) {
        Validate.notNull(block, "block cannot be null");

        BoundingBox boundingBox = block.getBoundingBox();
        double minX = boundingBox.getMinX();
        double minY = boundingBox.getMinY();
        double minZ = boundingBox.getMinZ();
        double maxX = boundingBox.getMaxX();
        double maxY = boundingBox.getMaxY();
        double maxZ = boundingBox.getMaxZ();
        return new FunnyBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static FunnyBox of(Vector corner1, Vector corner2) {
        Validate.notNull(corner1, "corner1 cannot be null");
        Validate.notNull(corner2, "corner2 cannot be null");

        return new FunnyBox(
                corner1.getX(), corner1.getY(), corner1.getZ(),
                corner2.getX(), corner2.getY(), corner2.getZ()
        );
    }

    public static FunnyBox of(Location corner1, Location corner2) {
        Validate.notNull(corner1, "corner1 cannot be null");
        Validate.notNull(corner2, "corner2 cannot be null");
        Validate.isTrue(Objects.equals(corner1.getWorld(), corner2.getWorld()), "Locations from different worlds!");

        return new FunnyBox(
                corner1.getX(), corner1.getY(), corner1.getZ(),
                corner2.getX(), corner2.getY(), corner2.getZ()
        );
    }

    public static FunnyBox of(Vector center, double x, double y, double z) {
        Validate.notNull(center, "center cannot be null");
        return new FunnyBox(
                center.getX() - x, center.getY() - y, center.getZ() - z,
                center.getX() + x, center.getY() + y, center.getZ() + z
        );
    }

    public static FunnyBox of(Location center, double x, double y, double z) {
        Validate.notNull(center, "center cannot be null");
        return new FunnyBox(
                center.getX() - x, center.getY() - y, center.getZ() - z,
                center.getX() + x, center.getY() + y, center.getZ() + z
        );
    }

    private static FunnyBox ofBlock(Block block) {
        return new FunnyBox(
                block.getX(), block.getY(), block.getZ(),
                block.getX() + 1, block.getY() + 1, block.getZ() + 1
        );
    }

    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public FunnyBox() {
    }

    public FunnyBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.resize(x1, y1, z1, x2, y2, z2);
    }

    public FunnyBox resize(double x1, double y1, double z1, double x2, double y2, double z2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(y1, "y1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(y2, "y2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");

        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);

        return this;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMinZ() {
        return this.minZ;
    }

    public Vector getMin() {
        return new Vector(this.minX, this.minY, this.minZ);
    }

    public double getMaxX() {
        return this.maxX;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public double getMaxZ() {
        return this.maxZ;
    }

    public Vector getMax() {
        return new Vector(this.maxX, this.maxY, this.maxZ);
    }

    public double getWidthX() {
        return (this.maxX - this.minX);
    }

    public double getWidthZ() {
        return (this.maxZ - this.minZ);
    }

    public double getHeight() {
        return (this.maxY - this.minY);
    }

    public double getVolume() {
        return (this.getHeight() * this.getWidthX() * this.getWidthZ());
    }

    public double getCenterX() {
        return (this.minX + (this.getWidthX() * 0.5D));
    }

    public double getCenterY() {
        return (this.minY + (this.getHeight() * 0.5D));
    }

    public double getCenterZ() {
        return (this.minZ + (this.getWidthZ() * 0.5D));
    }

    public Vector getCenter() {
        return new Vector(this.getCenterX(), this.getCenterY(), this.getCenterZ());
    }

    public FunnyBox copy(FunnyBox other) {
        Validate.notNull(other, "other cannot be null");
        return this.resize(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public FunnyBox expand(double negativeX, double negativeY, double negativeZ, double positiveX, double positiveY, double positiveZ) {
        if ((negativeX == 0.0d) && (negativeY == 0.0d) && (negativeZ == 0.0d) &&
                (positiveX == 0.0d) && (positiveY == 0.0d) && (positiveZ == 0.0d)) {
            return this;
        }

        double newMinX = this.minX - negativeX;
        double newMinY = this.minY - negativeY;
        double newMinZ = this.minZ - negativeZ;
        double newMaxX = this.maxX + positiveX;
        double newMaxY = this.maxY + positiveY;
        double newMaxZ = this.maxZ + positiveZ;

        if (newMinX > newMaxX) {
            double centerX = this.getCenterX();

            if (newMaxX >= centerX) {
                newMinX = newMaxX;
            }
            else if (newMinX <= centerX) {
                newMaxX = newMinX;
            }
            else {
                newMinX = centerX;
                newMaxX = centerX;
            }
        }

        if (newMinY > newMaxY) {
            double centerY = this.getCenterY();

            if (newMaxY >= centerY) {
                newMinY = newMaxY;
            }
            else if (newMinY <= centerY) {
                newMaxY = newMinY;
            }
            else {
                newMinY = centerY;
                newMaxY = centerY;
            }
        }

        if (newMinZ > newMaxZ) {
            double centerZ = this.getCenterZ();

            if (newMaxZ >= centerZ) {
                newMinZ = newMaxZ;
            }
            else if (newMinZ <= centerZ) {
                newMaxZ = newMinZ;
            }
            else {
                newMinZ = centerZ;
                newMaxZ = centerZ;
            }
        }

        return this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public FunnyBox expand(double x, double y, double z) {
        return this.expand(x, y, z, x, y, z);
    }

    public FunnyBox expand(Vector expansion) {
        Validate.notNull(expansion, "expansion cannot be null");

        double x = expansion.getX();
        double y = expansion.getY();
        double z = expansion.getZ();

        return this.expand(x, y, z, x, y, z);
    }

    public FunnyBox expand(double expansion) {
        return this.expand(expansion, expansion, expansion, expansion, expansion, expansion);
    }

    public FunnyBox expand(double dirX, double dirY, double dirZ, double expansion) {
        if (expansion == 0.0d) {
            return this;
        }

        if ((dirX == 0.0d) && (dirY == 0.0d) && (dirZ == 0.0d)) {
            return this;
        }

        double negativeX = ((dirX < 0.0d) ? (-dirX * expansion) : 0.0d);
        double negativeY = ((dirY < 0.0d) ? (-dirY * expansion) : 0.0d);
        double negativeZ = ((dirZ < 0.0d) ? (-dirZ * expansion) : 0.0d);
        double positiveX = ((dirX > 0.0d) ? (dirX * expansion) : 0.0d);
        double positiveY = ((dirY > 0.0d) ? (dirY * expansion) : 0.0d);
        double positiveZ = ((dirZ > 0.0d) ? (dirZ * expansion) : 0.0d);

        return this.expand(negativeX, negativeY, negativeZ, positiveX, positiveY, positiveZ);
    }

    public FunnyBox expand(Vector direction, double expansion) {
        Validate.notNull(direction, "expansion cannot be null");
        return this.expand(direction.getX(), direction.getY(), direction.getZ(), expansion);
    }

    public FunnyBox expand(BlockFace blockFace, double expansion) {
        Validate.notNull(blockFace, "blockFace cannot be null");

        if (blockFace == BlockFace.SELF) {
            return this;
        }

        return this.expand(blockFace.getDirection(), expansion);
    }

    public FunnyBox expandDirectional(double dirX, double dirY, double dirZ) {
        return this.expand(dirX, dirY, dirZ, 1.0d);
    }

    public FunnyBox expandDirectional(Vector direction) {
        Validate.notNull(direction, "direction cannot be null");
        return this.expand(direction.getX(), direction.getY(), direction.getZ(), 1.0d);
    }

    public FunnyBox expandDirectional(Vector direction1, Vector direction2) {
        Validate.notNull(direction1, "direction1 cannot be null");
        Validate.notNull(direction2, "direction2 cannot be null");

        return this.expand(direction1.getX(), direction1.getY(), direction1.getZ(), 1.0d)
                .expand(direction2.getX(), direction2.getY(), direction2.getZ(), 1.0d);
    }

    public FunnyBox union(double posX, double posY, double posZ) {
        double newMinX = Math.min(this.minX, posX);
        double newMinY = Math.min(this.minY, posY);
        double newMinZ = Math.min(this.minZ, posZ);
        double newMaxX = Math.max(this.maxX, posX);
        double newMaxY = Math.max(this.maxY, posY);
        double newMaxZ = Math.max(this.maxZ, posZ);

        if ((newMinX == this.minX) && (newMinY == this.minY) && (newMinZ == this.minZ) &&
                (newMaxX == this.maxX) && (newMaxY == this.maxY) && (newMaxZ == this.maxZ)) {
            return this;
        }

        return this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public FunnyBox union(Vector position) {
        Validate.notNull(position, "position cannot be null");
        return this.union(position.getX(), position.getY(), position.getZ());
    }

    public FunnyBox union(Location position) {
        Validate.notNull(position, "position cannot be null");
        return this.union(position.getX(), position.getY(), position.getZ());
    }

    public FunnyBox union(FunnyBox other) {
        Validate.notNull(other, "other cannot be null");
        if (this.contains(other)) {
            return this;
        }

        double newMinX = Math.min(this.minX, other.minX);
        double newMinY = Math.min(this.minY, other.minY);
        double newMinZ = Math.min(this.minZ, other.minZ);
        double newMaxX = Math.max(this.maxX, other.maxX);
        double newMaxY = Math.max(this.maxY, other.maxY);
        double newMaxZ = Math.max(this.maxZ, other.maxZ);

        return this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public FunnyBox intersection(FunnyBox other) {
        Validate.notNull(other, "other cannot be null");
        Validate.isTrue(this.overlaps(other), "boxes do not overlap");

        double newMinX = Math.max(this.minX, other.minX);
        double newMinY = Math.max(this.minY, other.minY);
        double newMinZ = Math.max(this.minZ, other.minZ);
        double newMaxX = Math.min(this.maxX, other.maxX);
        double newMaxY = Math.min(this.maxY, other.maxY);
        double newMaxZ = Math.min(this.maxZ, other.maxZ);

        return this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public FunnyBox shift(double shiftX, double shiftY, double shiftZ) {
        if ((shiftX == 0.0d) && (shiftY == 0.0d) && (shiftZ == 0.0d)) {
            return this;
        }

        return this.resize(
                this.minX + shiftX, this.minY + shiftY, this.minZ + shiftZ,
                this.maxX + shiftX, this.maxY + shiftY, this.maxZ + shiftZ
        );
    }

    public FunnyBox shift(Vector shift) {
        Validate.notNull(shift, "shift cannot be null");
        return this.shift(shift.getX(), shift.getY(), shift.getZ());
    }

    public FunnyBox shift(Location shift) {
        Validate.notNull(shift, "shift cannot be null");
        return this.shift(shift.getX(), shift.getY(), shift.getZ());
    }

    private boolean overlaps(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return (this.minX < maxX) && (this.maxX > minX) &&
                (this.minY < maxY) && (this.maxY > minY) &&
                (this.minZ < maxZ) && (this.maxZ > minZ);
    }

    public boolean overlaps(FunnyBox other) {
        Validate.notNull(other, "other cannot be null");
        return this.overlaps(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean overlaps(Vector min, Vector max) {
        Validate.notNull(min, "min cannot be null");
        Validate.notNull(max, "max cannot be null");

        double x1 = min.getX();
        double y1 = min.getY();
        double z1 = min.getZ();
        double x2 = max.getX();
        double y2 = max.getY();
        double z2 = max.getZ();

        return this.overlaps(
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        );
    }

    public boolean contains(double x, double y, double z) {
        return (x >= this.minX) && (x < this.maxX) &&
                (y >= this.minY) && (y < this.maxY) &&
                (z >= this.minZ) && (z < this.maxZ);
    }

    public boolean contains(Vector position) {
        Validate.notNull(position, "position cannot be null");
        return this.contains(position.getX(), position.getY(), position.getZ());
    }

    public boolean contains(Location position) {
        Validate.notNull(position, "position cannot be null");
        return this.contains(position.getX(), position.getY(), position.getZ());
    }

    private boolean contains(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return (this.minX <= minX) && (this.maxX >= maxX) &&
                (this.minY <= minY) && (this.maxY >= maxY) &&
                (this.minZ <= minZ) && (this.maxZ >= maxZ);
    }

    public boolean contains(FunnyBox other) {
        Validate.notNull(other, "Other bounding box cannot be null");
        return this.contains(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean contains(Vector min, Vector max) {
        Validate.notNull(min, "min cannot be null");
        Validate.notNull(max, "max cannot be null");

        double x1 = min.getX();
        double y1 = min.getY();
        double z1 = min.getZ();
        double x2 = max.getX();
        double y2 = max.getY();
        double z2 = max.getZ();

        return this.contains(
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        );
    }

    @Nullable
    public Vector rayTrace(Vector start, Vector direction, double stepSize, double maxDistance) {
        Validate.notNull(start, "start cannot be null");
        checkFinite(start);

        Validate.notNull(direction, "direction cannot be null");
        checkFinite(direction);

        Validate.isTrue(direction.lengthSquared() > 0, "directions's magnitude is 0");
        if (maxDistance < 0.0d) {
            return null;
        }

        Vector delta = direction.clone().normalize().multiply(stepSize);
        Vector current = start.clone();
        for (double step = 1; step <= Math.floor(maxDistance / stepSize); step++) {
            current.add(delta);
            if (this.contains(current)) {
                return current;
            }
        }
        return null;
    }

    private static void checkFinite(Vector vector) throws IllegalArgumentException {
        NumberConversions.checkFinite(vector.getX(), "x not finite");
        NumberConversions.checkFinite(vector.getY(), "y not finite");
        NumberConversions.checkFinite(vector.getZ(), "z not finite");
    }

}
