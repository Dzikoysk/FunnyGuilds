package net.dzikoysk.funnyguilds.nms.v1_20R5.playerlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.ProtocolDependentHelper;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_20R5PlayerList implements PlayerList {
    private static final GameType DEFAULT_GAME_MODE = GameType.SURVIVAL;
    private static final Component EMPTY_COMPONENT = Component.empty();

    private static final Field playerInfoEntriesField;

    static {
        try {
            playerInfoEntriesField = ClientboundPlayerInfoUpdatePacket.class.getDeclaredField("c");
            playerInfoEntriesField.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            throw new IllegalStateException("missing 'b' field in ClientboundPlayerInfoUpdatePacket", ex);
        }
    }

    private final int cellCount;
    private final GameProfile[] profileCache = new GameProfile[PlayerListConstants.DEFAULT_CELL_COUNT];
    private boolean firstPacket = true;

    public V1_20R5PlayerList(int cellCount) {
        this.cellCount = cellCount;
    }

    @Override
    public void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures, int ping,
                     Set<Integer> forceUpdateSlots) {
        List<Packet<?>> packets = Lists.newArrayList();
        List<Entry> addPlayerList = Lists.newArrayList();
        List<Entry> updatePlayerList = Lists.newArrayList();

        try {
            for (int i = 0; i < this.cellCount; i++) {
                String paddedIdentifier = StringUtils.leftPad(String.valueOf(i), 2, '0');
                String gameProfileName = ProtocolDependentHelper.getGameProfileNameBasedOnPlayerProtocolVersion(player, paddedIdentifier, paddedIdentifier);

                if (this.profileCache[i] == null) {
                    this.profileCache[i] = new GameProfile(
                            UUID.fromString(String.format(PlayerListConstants.UUID_PATTERN, paddedIdentifier)),
                            gameProfileName
                    );
                }

                String text = playerListCells[i];
                GameProfile gameProfile = this.profileCache[i];
                Component component = CraftChatMessage.fromString(text, false)[0];

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    SkinTexture texture = cellTextures[i];
                    if (texture != null) {
                        gameProfile.getProperties().removeAll("textures");
                        gameProfile.getProperties().put("textures", texture.getProperty());
                    }
                }

                Entry playerInfoData = new Entry(
                        gameProfile.getId(),
                        gameProfile,
                        true,
                        ping,
                        DEFAULT_GAME_MODE,
                        component,
                        null
                );

                if (this.firstPacket || forceUpdateSlots.contains(i)) {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (this.firstPacket) {
                this.firstPacket = false;
            }

            ClientboundPlayerInfoUpdatePacket addPlayerPacket = createPlayerInfoPacket(
                    EnumSet.of(
                            Action.ADD_PLAYER,
                            Action.UPDATE_GAME_MODE,
                            Action.UPDATE_LISTED,
                            Action.UPDATE_LATENCY,
                            Action.UPDATE_DISPLAY_NAME
                    ),
                    addPlayerList
            );
            packets.add(addPlayerPacket);

            ClientboundPlayerInfoUpdatePacket updatePlayerPacket = createPlayerInfoPacket(
                    EnumSet.of(Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME),
                    updatePlayerList
            );
            packets.add(updatePlayerPacket);

            boolean headerNotEmpty = !header.isEmpty();
            boolean footerNotEmpty = !footer.isEmpty();

            if (headerNotEmpty || footerNotEmpty) {
                Component headerComponent = EMPTY_COMPONENT;
                Component footerComponent = EMPTY_COMPONENT;

                if (headerNotEmpty) {
                    headerComponent = CraftChatMessage.fromStringOrNull(header, true);
                }

                if (footerNotEmpty) {
                    footerComponent = CraftChatMessage.fromStringOrNull(footer, true);
                }

                ClientboundTabListPacket headerFooterPacket =
                        new ClientboundTabListPacket(headerComponent, footerComponent);
                packets.add(headerFooterPacket);
            }

            for (Packet<?> packet : packets) {
                ((CraftPlayer) player).getHandle().connection.send(packet);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to send PlayerList for player " + player.getName(), exception);
        }
    }

    private ClientboundPlayerInfoUpdatePacket createPlayerInfoPacket(EnumSet<Action> actions,
                                                                     List<Entry> entries) {
        // NOTE: this whole hack exists just because Mojang does stupid things and collects list of entries
        //       into an immutable list without any ability to modify or pass direct entries through constructor.
        ClientboundPlayerInfoUpdatePacket playerInfoPacket =
                new ClientboundPlayerInfoUpdatePacket(actions, List.<Entry>of());

        try {
            playerInfoEntriesField.set(playerInfoPacket, entries);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException("could not create player info packet", ex);
        }

        return playerInfoPacket;
    }
}
