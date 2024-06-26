package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.Dummy;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTag;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class UserCache {

    private final User user;

    private IndividualPlayerList playerList;
    private Option<Scoreboard> scoreboard = Option.none();
    private Option<IndividualNameTag> nameTag = Option.none();
    private Option<Dummy> dummy = Option.none();

    private BukkitTask teleportation;
    private long notificationTime;
    private boolean spy;

    public UserCache(User user) {
        this.user = user;
    }

    public Option<IndividualPlayerList> getPlayerList() {
        return Option.of(this.playerList);
    }

    public void setPlayerList(IndividualPlayerList playerList) {
        this.playerList = playerList;
    }

    public synchronized Option<Scoreboard> getScoreboard() {
        return this.scoreboard;
    }

    public synchronized void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = Option.of(scoreboard);
    }

    public Option<IndividualNameTag> getIndividualNameTag() {
        return this.nameTag;
    }

    public void setIndividualNameTag(@Nullable IndividualNameTag nameTag) {
        this.nameTag = Option.of(nameTag);
    }

    public Option<Dummy> getDummy() {
        return this.dummy;
    }

    public void setDummy(@Nullable Dummy dummy) {
        this.dummy = Option.of(dummy);
    }

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public void setTeleportation(BukkitTask teleportation) {
        this.teleportation = teleportation;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }

    public void setNotificationTime(long notification) {
        this.notificationTime = notification;
    }

    public boolean isSpy() {
        return this.spy;
    }

    public boolean toggleSpy() {
        this.spy = !this.spy;
        return this.spy;
    }

}
