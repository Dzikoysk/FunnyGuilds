package net.dzikoysk.funnyguilds.feature.scoreboard;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.config.ScoreboardConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public abstract class AbstractScoreboardHandler<T> {

    protected final ScoreboardConfiguration scoreboardConfiguration;
    protected final UserManager userManager;
    protected final ScoreboardService scoreboardService;

    private final Set<Pair<UpdateData, UpdateData>> usersToUpdate = new HashSet<>();
    private final Queue<Pair<UpdateData, UpdateData>> updateQueue = new LinkedList<>();
    private final Queue<Pair<UpdateData, UpdateData>> highPriorityUpdateQueue = new LinkedList<>();

    protected AbstractScoreboardHandler(ScoreboardConfiguration scoreboardConfiguration, UserManager userManager, ScoreboardService scoreboardService) {
        this.scoreboardConfiguration = scoreboardConfiguration;
        this.userManager = userManager;
        this.scoreboardService = scoreboardService;
    }

    protected abstract T getOrCreateData(Player player, User user);

    protected abstract void update(UpdateData observerData, UpdateData targetData);

    private void queueUpdate(UpdateData observerData, UpdateData targetData, boolean highPriority) {
        Pair<UpdateData, UpdateData> pair = Pair.of(observerData, targetData);

        if (highPriority) {
            this.highPriorityUpdateQueue.add(pair);
        } else if (!this.usersToUpdate.contains(pair)) {
            this.updateQueue.add(pair);
            this.usersToUpdate.add(pair);
        }
    }

    public boolean popAndUpdate() {
        Pair<UpdateData, UpdateData> updatePair = this.highPriorityUpdateQueue.poll();
        if (updatePair == null) {
            updatePair = this.updateQueue.poll();
            if (updatePair == null) {
                return false;
            }
            this.usersToUpdate.remove(updatePair);
        }
        this.update(updatePair.getFirst(), updatePair.getSecond());
        return true;
    }

    // Update everyone to everyone
    public void updatePlayers(boolean highPriority) {
        List<UpdateData> toUpdate = this.getOnlinePlayersToUpdate();
        for (int observerIndex = 0; observerIndex < toUpdate.size(); observerIndex++) {
            UpdateData observerData = toUpdate.get(observerIndex);
            for (int targetIndex = observerIndex; targetIndex < toUpdate.size(); targetIndex++) {
                UpdateData targetData = toUpdate.get(targetIndex);
                this.queueUpdate(observerData, targetData, highPriority);
            }
        }
    }

    // Update specific observer to everyone (targets) and everyone to specific observer
    public void updatePlayer(Player observerPlayer, User observerUser, boolean highPriority) {
        UpdateData observerData = observerPlayer != null && observerUser.isOnline()
                ? new UpdateData(observerUser, observerPlayer, this.getOrCreateData(observerPlayer, observerUser))
                : new UpdateData(observerUser, null, null);

        this.getOnlinePlayersToUpdate().forEach(targetData -> this.queueUpdate(observerData, targetData, highPriority));
    }

    private List<UpdateData> getOnlinePlayersToUpdate() {
        return PandaStream.of(Bukkit.getOnlinePlayers())
                .mapOpt(player -> this.userManager.findByUuid(player.getUniqueId())
                        .map(user -> new UpdateData(user, player, this.getOrCreateData(player, user))))
                .toList();
    }

    protected class UpdateData {

        private final User user;

        private final WeakReference<Player> playerRef;
        private final WeakReference<T> dataRef;

        public UpdateData(User user, @Nullable Player player, @Nullable T data) {
            this.user = user;
            this.playerRef = new WeakReference<>(player);
            this.dataRef = new WeakReference<>(data);
        }

        public User getUser() {
            return this.user;
        }

        public Option<Player> getPlayer() {
            return Option.of(this.playerRef.get());
        }

        public Option<T> getData() {
            return Option.of(this.dataRef.get());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof AbstractScoreboardHandler.UpdateData)) {
                return false;
            }

            UpdateData updateData = (UpdateData) obj;
            return this.user.equals(updateData.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.user);
        }

    }

}
