package net.dzikoysk.funnyguilds.nms.api;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public final class ProtocolDependentHelper {

    private static final int V1_19_3_PROTOCOL_VERSION = 761;
    private static boolean viaApiAvailable;

    static {
        try {
            Class.forName("com.viaversion.viaversion.api.ViaAPI");
            viaApiAvailable = true;
        } catch (ClassNotFoundException ignored) {}
    }

    private ProtocolDependentHelper() {}

    public static String getGameProfileNameBasedOnPlayerProtocolVersion(Player player, String paddedIdentifier, String defaultIdentifier) {
        // NOTE: Sorting changed in 1.19.3 and GameProfile name is being included in the sorting process.
        //       To mitigate it when server is using ViaVersion and client protocol version mismatches with the server protocol version,
        //       we're checking the protocol version that player uses and return proper identifier for specific version.

        int playerProtocolVersion = getPlayerProtocolVersion(player);
        if (playerProtocolVersion >= V1_19_3_PROTOCOL_VERSION) {
            return "~" + paddedIdentifier;
        }

        return defaultIdentifier.trim().isEmpty()
                    ? defaultIdentifier
                    : "~" + defaultIdentifier;
    }

    private static int getPlayerProtocolVersion(Player player) {
        if (!viaApiAvailable) {
            return -1;
        }

        return getPlayerProtocolVersionImpl(player);
    }

    private static int getPlayerProtocolVersionImpl(Player player) {
        return Via.getAPI().getPlayerVersion(player.getUniqueId());
    }

}
