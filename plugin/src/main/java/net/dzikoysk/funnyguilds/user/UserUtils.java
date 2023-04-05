package net.dzikoysk.funnyguilds.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import panda.std.Option;
import panda.std.Result;

public final class UserUtils {

    private UserUtils() {
    }

    /**
     * Gets the set of usernames (with tags to format) from collection of users.
     *
     * @param users collection of users
     * @return set of usernames (with tags to format)
     */
    public static Set<String> getOnlineNames(Collection<User> users) {
        Set<String> set = new HashSet<>();
        for (User user : users) {
            set.add(user.isOnline() ? "<online>" + user.getName() + "</online>" : user.getName());
        }

        return set;
    }

    public static String getUserPosition(PluginConfiguration pluginConfiguration, @Nullable User user) {
        if (user == null) {
            return "";
        }

        if (user.isOwner()) {
            return pluginConfiguration.chatPositionLeader.getValue();
        }

        if (user.isDeputy()) {
            return pluginConfiguration.chatPositionDeputy.getValue();
        }

        return pluginConfiguration.chatPositionMember.getValue();
    }

    /**
     * Check if user file is correct and if not - try migrating it
     *
     * @param config plugin configuration
     * @param file   user file
     * @return A final (source or migrated) user file
     */
    public static Option<File> checkUserFile(PluginConfiguration config, File file) {
        String filenameWithoutExtension = FunnyStringUtils.removeEnd(file.getName(), ".yml");
        if (FunnyValidator.validateUUID(filenameWithoutExtension)) {
            return Option.of(file);
        }

        if (FunnyValidator.validateUsername(config, filenameWithoutExtension) != NameResult.VALID) {
            return migrateUserFile(file)
                    .onError(error -> FunnyGuilds.getPluginLogger().error(error))
                    .toOption();
        }

        return Option.none();
    }

    /**
     * Try migrating a user file to a new name
     *
     * @param file user file
     * @return Result with migrated user file or a migration error message
     */
    public static Result<File, String> migrateUserFile(File file) {
        YamlWrapper wrapper = new YamlWrapper(file);
        String id = wrapper.getString("uuid");

        if (id == null || !FunnyValidator.validateUUID(id)) {
            return Result.error("Migration of user file '" + file.getName() + "' failed, UUID is invalid");
        }

        Path source = file.toPath();
        Path target = source.resolveSibling(String.format("%s.yml", id));

        if (Files.exists(target)) {
            return Result.ok(target.toFile());
        }

        return Result.attempt(IOException.class, () -> Files.move(source, target, StandardCopyOption.REPLACE_EXISTING).toFile())
                .mapErr(error -> "Could not move file '" + source + "' to '" + target + "': " + error.getMessage());
    }

}
