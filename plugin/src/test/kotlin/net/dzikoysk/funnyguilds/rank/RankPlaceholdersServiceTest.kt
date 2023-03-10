package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.RawString
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class RankPlaceholdersServiceTest : FunnyGuildsSpec() {

    @Test
    fun `should parse ptop & gtop placeholders`() {
        // given: user & guild on top of the ranking
        val user = userManager.createFake(UUID.randomUUID(), "PandaMember")
        val guild = guildManager.addGuild(Guild("OnlyPanda", "OP"))
        guild.addMember(user)

        // then: the ranking should be recalculated
        RankRecalculationTask(funnyGuilds).run()

        config.ptopOffline = RawString("")
        config.top.format.ptop = RawString(" {VALUE}")
        config.top.format.gtop = RawString(" {VALUE}")

        // when: the PTOP/GTOP placeholder is requested to parse
        val userRank = rankPlaceholdersService.formatTop("{PTOP-POINTS-1}", user)
        val guildRank = rankPlaceholdersService.formatTop("{GTOP-AVG_POINTS-1}", user)

        // then: the result should match the configured pattern
        assertEquals("PandaMember 1000", userRank)
        assertEquals("OP 1000", guildRank)
    }

}