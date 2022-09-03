package net.dzikoysk.funnyguilds.shared

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.TimeInflection.Case
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class TimeUtilsTest : FunnyGuildsSpec() {

    @Test
    fun `should parse time`() {
        val durationOne = TimeUtils.parseTime("6d15h19m12s").toMillis()
        val durationTwo = TimeUtils.parseTime("2d12s").toMillis()

        assertEquals(durationOne, 573552 * 1000L)
        assertEquals(durationTwo, 172812 * 1000L)
    }

    @Test
    fun `test short format time`() {
        val durationOne = Duration.ofDays(3).plusHours(5).plusMinutes(10).plusSeconds(20);
        val durationTwo = Duration.ofDays(3)
        val durationThree = Duration.ofSeconds(20)

        assertEquals("3d 5h 10m 20s", TimeUtils.formatTimeShort(messages, durationOne))
        assertEquals("3d", TimeUtils.formatTimeShort(messages, durationTwo))
        assertEquals("20s", TimeUtils.formatTimeShort(messages, durationThree))
    }

    @Test
    fun `test long format time nominative`() {
        val durationOne = Duration.ofDays(2).plusHours(15).plusMinutes(59).plusSeconds(1);
        val durationTwo = Duration.ofDays(2)
        val durationThree = Duration.ofSeconds(20)

        assertEquals("2 dni 15 godzin 59 minut 1 sekunda", TimeUtils.formatTime(messages, durationOne, Case.NOMINATIVE))
        assertEquals("2 dni", TimeUtils.formatTime(messages, durationTwo, Case.NOMINATIVE))
        assertEquals("20 sekund", TimeUtils.formatTime(messages, durationThree, Case.NOMINATIVE))
    }

    @Test
    fun `test long format time accusative`() {
        val durationOne = Duration.ofDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);

        assertEquals("1 dzień 1 godzinę 1 minutę 1 sekundę", TimeUtils.formatTime(messages, durationOne, Case.ACCUSATIVE))
    }

}