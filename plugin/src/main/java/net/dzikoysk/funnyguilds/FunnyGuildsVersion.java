package net.dzikoysk.funnyguilds;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import org.bukkit.command.CommandSender;

public final class FunnyGuildsVersion {

    private static final Gson GSON = new Gson();

    private static final String GH_COMMITS_URL = "https://api.github.com/repos/FunnyGuilds/FunnyGuilds/commits";
    private static final String VERSION_FILE_URL = "https://funnyguilds.dzikoysk.net/latest.info";
    private static final String NIGHTLY_VERSION_FILE_URL = "https://raw.githubusercontent.com/FunnyGuilds/FunnyGuilds/master/updater-nightly.txt";
    private static final String GITHUB_URL = "https://github.com/funnyguilds/funnyguilds";
    private static final String DISCORD_URL = "https://discord.com/invite/CYvyq3u";

    private final FunnyGuilds funnyGuilds;
    private final String fullVersion;
    private final String mainVersion;
    private final String commitHash;

    public FunnyGuildsVersion(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;

        String version = funnyGuilds.getDescription().getVersion();
        this.fullVersion = version;
        this.mainVersion = version.substring(0, version.lastIndexOf('-'));
        this.commitHash = version.substring(version.lastIndexOf('-') + 1);
    }

    public void isNewAvailable(CommandSender sender, boolean force) {
        if (!this.funnyGuilds.getPluginConfiguration().updateInfo && !force) {
            return;
        }

        if (!sender.hasPermission("funnyguilds.admin")) {
            return;
        }

        this.funnyGuilds.getServer().getScheduler().runTaskAsynchronously(this.funnyGuilds, () -> {
            if (this.funnyGuilds.getPluginConfiguration().updateNightlyInfo) {
                try {
                    String ghResponse = FunnyIOUtils.getContent(GH_COMMITS_URL);
                    JsonArray ghCommits = GSON.fromJson(ghResponse, JsonArray.class);

                    if (ghCommits.size() == 0) {
                        return;
                    }

                    String latestNightlyVersion = FunnyIOUtils.getContent(NIGHTLY_VERSION_FILE_URL);
                    JsonObject latestCommit = ghCommits.get(0).getAsJsonObject();
                    String latestCommitHash = latestCommit.get("sha").getAsString().substring(0, 7);

                    String latestNightly = latestNightlyVersion + "-" + latestCommitHash;

                    if (!this.commitHash.equalsIgnoreCase(latestCommitHash)) {
                        this.printNewVersionAvailable(sender, latestNightly, true);
                    }
                }
                catch (Exception exception) {
                    FunnyGuilds.getPluginLogger().update("Could not retrieve latest nightly version!");
                    FunnyGuilds.getPluginLogger().update(Throwables.getStackTraceAsString(exception));
                }
            }
            else {
                String latestRelease = FunnyIOUtils.getContent(VERSION_FILE_URL);
                if (latestRelease.isEmpty()) {
                    return;
                }

                if (latestRelease.contains("Warning:")) {
                    FunnyGuilds.getPluginLogger().warning(latestRelease);
                    return;
                }

                if (!this.mainVersion.equalsIgnoreCase(latestRelease)) {
                    this.printNewVersionAvailable(sender, latestRelease, false);
                }
            }
        });
    }

    private void printNewVersionAvailable(CommandSender sender, String latest, boolean isNightly) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{VERSION_TYPE}", isNightly ? "Nightly" : "")
                .register("{CURRENT_VERSION}", this.fullVersion)
                .register("{NEWEST_VERSION}", latest)
                .register("{GITHUB_LINK}", GITHUB_URL)
                .register("{DISCORD_LINK}", DISCORD_URL);

        FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.system.newVersionAvailable)
                .receiver(sender)
                .with(formatter)
                .send();
    }

    public String getFullVersion() {
        return this.fullVersion;
    }

    public String getMainVersion() {
        return this.mainVersion;
    }

    public String getCommitHash() {
        return this.commitHash;
    }

}
