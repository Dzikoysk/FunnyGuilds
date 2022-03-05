package net.dzikoysk.funnyguilds.feature.placeholders

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import panda.std.Pair

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock

@CompileStatic
class PlaceholdersTest extends FunnyGuildsSpec {

    @Override
    @BeforeEach
    void prepareBukkit() {
        Player player = mock(Player.class);

        mockedBukkit.when(() -> Bukkit.getPlayer(any(UUID.class))).thenAnswer(invocation -> {
            if (UUID.nameUUIDFromBytes("online".getBytes()).equals(invocation.getArguments()[0])) {
                return player
            }

            return null
        })
    }

    @Test
    void 'test ONLINE placeholder' () {
        def text = "§7offline - <online>online</online> - offline - <online>online</online> - offline"
        def formatter = Placeholders.ONLINE.toFormatter(ChatUtils.getLastColorBefore(text, "<online>"))

        text = formatter.format text

        assertEquals '§7offline - §aonline§7 - offline - §aonline§7 - offline', text
    }

    @Test
    void 'test GUILD_MEMBERS_COLOR_CONTEXT placeholder' () {
        def guild = guildManager.addGuild(new Guild('guild', 'TEST'))
        def user1 = userManager.create(UUID.randomUUID(), 'user1')
        def user2 = userManager.create(UUID.nameUUIDFromBytes('online'.getBytes()), 'user2')
        def user3 = userManager.create(UUID.randomUUID(), 'user3')

        guild.addMember(user1)
        guild.addMember(user2)
        guild.addMember(user3)

        def text1 = "§7{MEMBERS}"
        text1 = Placeholders.GUILD_MEMBERS_COLOR_CONTEXT
                .format(text1, Pair.of(ChatUtils.getLastColorBefore(text1, "{MEMBERS}"), guild))

        assertEquals '§7user1, §auser2§7, user3', text1

        def text2 = "§c{MEMBERS}"
        text2 = Placeholders.GUILD_MEMBERS_COLOR_CONTEXT
                .format(text2, Pair.of(ChatUtils.getLastColorBefore(text2, "{MEMBERS}"), guild))

        assertEquals '§cuser1, §auser2§c, user3', text2

        def text3 = "§a{MEMBERS}"
        text3 = Placeholders.GUILD_MEMBERS_COLOR_CONTEXT
                .format(text3, Pair.of(ChatUtils.getLastColorBefore(text3, "{MEMBERS}"), guild))

        assertEquals '§auser1, §auser2§a, user3', text3
    }

}
