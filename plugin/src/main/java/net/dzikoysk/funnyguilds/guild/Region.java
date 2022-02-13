package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class Region extends AbstractMutableEntity {

    private String name;
    private Guild guild;

    private World world;
    private Location center;
    private int size;
    private int enlarge;

    private Location firstCorner;
    private Location secondCorner;

    public Region(@NotNull String name) {
        this.name = name;
    }

    public Region(@NotNull Guild guild, @NotNull Location center, int size) {
        this(guild.getName());

        this.guild = guild;
        this.world = center.getWorld();
        this.center = center;
        this.size = size;

        this.update();
    }

    public synchronized void update() {
        super.markChanged();

        if (this.center == null) {
            return;
        }

        if (this.size < 1) {
            return;
        }

        if (this.world == null) {
            this.world = Bukkit.getWorlds().get(0);
        }

        if (this.world != null) {
            int lx = this.center.getBlockX() + this.size;
            int lz = this.center.getBlockZ() + this.size;

            int px = this.center.getBlockX() - this.size;
            int pz = this.center.getBlockZ() - this.size;

            Vector l = new Vector(lx, LocationUtils.getMinHeight(this.world), lz);
            Vector p = new Vector(px, this.world.getMaxHeight(), pz);

            this.firstCorner = l.toLocation(world);
            this.secondCorner = p.toLocation(world);
        }
    }

    public boolean isIn(Location location) {
        if (location == null || this.firstCorner == null || this.secondCorner == null) {
            return false;
        }

        if (this.world == null) {
            return false;
        }

        if (!this.world.equals(location.getWorld())) {
            return false;
        }

        if (location.getBlockX() > this.getLowerX() && location.getBlockX() < this.getUpperX()) {
            if (location.getBlockY() > this.getLowerY() && location.getBlockY() < this.getUpperY()) {
                return location.getBlockZ() > this.getLowerZ() && location.getBlockZ() < this.getUpperZ();
            }
        }

        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
        super.markChanged();
    }

    @Nullable
    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(@NotNull Guild guild) {
        this.guild = guild;
        super.markChanged();
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    @Nullable
    public Location getCenter() {
        return this.center;
    }

    @Nullable
    public Location getHeart() {
        Block heart = this.getHeartBlock();
        if(heart == null) {
            return null;
        }
        return this.getHeartBlock().getLocation();
    }

    @Nullable
    public Block getHeartBlock() {
        return Option.of(this.getCenter())
                .map(Location::getBlock)
                .map(block -> block.getRelative(BlockFace.DOWN))
                .getOrNull();
    }

    public void setCenter(@NotNull Location location) {
        this.center = location;
        this.world = location.getWorld();
        this.update();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.update();
    }

    public int getEnlarge() {
        return this.enlarge;
    }

    public void setEnlarge(int enlarge) {
        this.enlarge = enlarge;
        super.markChanged();
    }

    public int getUpperX() {
        return compareCoordinates(true, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getUpperY() {
        return compareCoordinates(true, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getUpperZ() {
        return compareCoordinates(true, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public int getLowerX() {
        return compareCoordinates(false, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getLowerY() {
        return compareCoordinates(false, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getLowerZ() {
        return compareCoordinates(false, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public Location getUpperCorner() {
        return new Location(this.world, this.getUpperX(), this.getUpperY(), this.getUpperZ());
    }

    public Location getLowerCorner() {
        return new Location(this.world, this.getLowerX(), this.getLowerY(), this.getLowerZ());
    }

    public Location getFirstCorner() {
        return this.firstCorner;
    }

    public Location getSecondCorner() {
        return this.secondCorner;
    }

    private int compareCoordinates(boolean upper, int a, int b) {
        if (upper) {
            return Math.max(b, a);
        }
        else {
            return Math.min(a, b);
        }
    }

    public FunnyBox toBox() {
        return FunnyBox.of(firstCorner, secondCorner);
    }

    @Override
    public EntityType getType() {
        return EntityType.REGION;
    }

    @Override
    public String toString() {
        return this.name;
    }

}