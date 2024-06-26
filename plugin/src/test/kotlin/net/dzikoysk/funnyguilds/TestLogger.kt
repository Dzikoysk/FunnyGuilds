package net.dzikoysk.funnyguilds

import org.slf4j.Logger

class TestLogger(rootLogger: Logger) : FunnyGuildsLogger(rootLogger) {

    override fun parser(content: String) {
    }

}