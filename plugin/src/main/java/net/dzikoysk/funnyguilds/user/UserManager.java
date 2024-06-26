package net.dzikoysk.funnyguilds.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import net.dzikoysk.funnyguilds.shared.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class UserManager {

    private final PluginConfiguration pluginConfiguration;
    private final Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();

    public UserManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public int countUsers() {
        return this.usersByUuid.size();
    }

    /**
     * Gets the copied set of users.
     *
     * @return set of users
     */
    public Set<User> getUsers() {
        return new HashSet<>(this.usersByUuid.values());
    }

    /**
     * Deletes all loaded users data
     */
    public void clearUsers() {
        this.usersByUuid.clear();
        this.usersByName.clear();
    }

    /**
     * Gets the set of users from collection of strings (names).
     *
     * @param names collection of names
     * @return set of users
     */
    public Set<User> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the user.
     *
     * @param uuid the universally unique identifier of user
     * @return the user
     */
    public Option<User> findByUuid(UUID uuid) {
        return Option.of(this.usersByUuid.get(uuid));
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of user
     * @return the user
     */
    public Option<User> findByName(String nickname) {
        return this.findByName(nickname, false);
    }

    /**
     * Gets the user.
     *
     * @param nickname   the name of user
     * @param ignoreCase ignore the case of the nickname
     * @return the user
     */
    public Option<User> findByName(String nickname, boolean ignoreCase) {
        User foundUser = this.usersByName.get(nickname);

        if (foundUser == null && ignoreCase) {
            foundUser = PandaStream.of(this.usersByName.entrySet())
                    .find(entry -> entry.getKey().equalsIgnoreCase(nickname))
                    .map(Map.Entry::getValue)
                    .orNull();
        }

        return Option.of(foundUser);
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public Option<User> findByPlayer(@NotNull Player player) {
        if (player.getUniqueId().version() == 2) {
            return Option.of(new User(player.getUniqueId(), player.getName(), new NPCUserProfile()));
        }

        return this.findByUuid(player.getUniqueId());
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public Option<User> findByPlayer(OfflinePlayer offlinePlayer) {
        return this.findByUuid(offlinePlayer.getUniqueId());
    }

    public User createFake(UUID uuid, String name) {
        return this.create(uuid, name, FakeUserProfile.offline());
    }

    public User createFake(UUID uuid, String name, FakeUserProfile profile) {
        return this.create(uuid, name, profile);
    }

    /**
     * Create the user and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link UserManager#findByUuid(UUID)}, {@link UserManager#findByName(String)} etc.
     *
     * @param uuid        the universally unique identifier which will be assigned to user
     * @param name        the nickname which will be assigned to User
     * @param userProfile the user profile which will be assigned to User
     * @return the user
     */
    public User create(UUID uuid, String name, UserProfile userProfile) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.notNull(userProfile, "userProfile can't be null!");
        Validate.isTrue(FunnyValidator.validateUsername(this.pluginConfiguration, name) == NameResult.VALID, "name is not valid!");

        User user = new User(uuid, name, userProfile);
        this.addUser(user);

        return user;
    }

    /**
     * Add user to storage. If you think you should use this method you probably shouldn't.
     *
     * @param user user to add
     */
    public void addUser(User user) {
        Validate.notNull(user, "user can't be null!");

        this.usersByUuid.put(user.getUUID(), user);
        this.usersByName.put(user.getName(), user);
    }

    /**
     * Remove user from storage. If you think you should use this method you probably shouldn't.
     *
     * @param user user to remove
     */
    public void removeUser(User user) {
        Validate.notNull(user, "user can't be null!");

        this.usersByUuid.remove(user.getUUID());
        this.usersByName.remove(user.getName());
    }

    /**
     * Update username for user.
     *
     * @param user        the user for which the nickname will be changed
     * @param newUsername the new nickname for user
     */
    public void updateUsername(User user, String newUsername) {
        Validate.notNull(user, "user can't be null!");

        this.usersByName.remove(user.getName());
        this.usersByName.put(newUsername, user);

        user.setName(newUsername);
    }

    /**
     * Checks if user with given nickname have ever played on a server.
     *
     * @param nickname the nickname of user to check if ever played on
     * @return if user with given name have ever played on a server
     */
    public boolean playedBefore(String nickname) {
        return this.playedBefore(nickname, false);
    }

    /**
     * Checks if user with given nickname have ever played on a server.
     *
     * @param nickname   the nickname of user to check if ever played on
     * @param ignoreCase ignore the case of the nickname
     * @return if user with given name have ever played on a server
     */
    public boolean playedBefore(String nickname, boolean ignoreCase) {
        return this.findByName(nickname, ignoreCase).isPresent();
    }

}