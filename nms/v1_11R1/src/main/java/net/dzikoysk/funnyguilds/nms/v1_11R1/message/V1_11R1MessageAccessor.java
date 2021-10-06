package net.dzikoysk.funnyguilds.nms.v1_11R1.message;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_11R1MessageAccessor implements MessageAccessor {
    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Player... players) {
        PacketPlayOutTitle titlePacket =
                new PacketPlayOutTitle(EnumTitleAction.TITLE, CraftChatMessage.fromString(titleMessage.getText(), false)[0]);
        PacketPlayOutTitle subtitlePacket =
                new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(titleMessage.getSubText(), false)[0]);
        PacketPlayOutTitle timesPacket =
                new PacketPlayOutTitle(titleMessage.getFadeInDuration(), titleMessage.getStayDuration(), titleMessage.getFadeOutDuration());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
        }
    }

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players) {
        this.sendTitleMessage(titleMessage, players.toArray(new Player[0]));
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        PacketPlayOutChat actionBarPacket = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionBarPacket);
        }
    }

    @Override
    public void sendActionBarMessage(String text, Collection<? extends Player> players) {
        this.sendActionBarMessage(text, players.toArray(new Player[0]));
    }
}
