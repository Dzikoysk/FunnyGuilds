package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.IntegerRange
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class RankUtilsTest extends FunnyGuildsSpec {

    @Test
    void 'should parse rank with guild name'() {
        // given: a guild on top of the ranking
        def guild = new Guild('OnlyPanda')
        guild.setTag('OP')

        def user = userManager.create(UUID.randomUUID(), 'name')
        guild.addMember(user)

        rankManager.update(guild)

        config.gtopPoints = ' {POINTS-FORMAT}'
        config.pointsFormat = [ new IntegerRange(0, Integer.MAX_VALUE): '{POINTS}' ]

        // when: the GTOP placeholder is requested to parse
        def rank = RankUtils.parseRank(config, new TablistConfiguration(), messages, rankManager, user, '{GTOP-1}')

        // then: the result should match the configured pattern
        assertEquals 'OP 1000', rank
    }

}
