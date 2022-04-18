package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.config.MessageConfiguration
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.guild.GuildRankManager
import net.dzikoysk.funnyguilds.guild.RegionManager
import net.dzikoysk.funnyguilds.rank.DefaultTops
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService
import net.dzikoysk.funnyguilds.user.UserManager
import net.dzikoysk.funnyguilds.user.UserRankManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mockStatic
import org.mockito.junit.jupiter.MockitoExtension
import java.util.logging.Logger

@ExtendWith(MockitoExtension::class)
open class FunnyGuildsSpec : BukkitSpec(){

    protected lateinit var mockedFunnyGuilds: MockedStatic<FunnyGuilds>

    @BeforeEach
    fun openMockedFunnyGuilds() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds::class.java)
    }

    @Mock
    lateinit var funnyGuilds: FunnyGuilds

    protected val funnyGuildsLogger = FunnyGuildsLogger(Logger.getLogger("TestLogger"))

    protected val config = PluginConfiguration()
    protected val messages = MessageConfiguration()
    protected val tablistConfig = TablistConfiguration()

    protected lateinit var userManager: UserManager
    protected lateinit var guildManager: GuildManager
    protected lateinit var userRankManager: UserRankManager
    protected lateinit var guildRankManager: GuildRankManager
    protected lateinit var regionManager: RegionManager

    protected lateinit var rankPlaceholdersService: RankPlaceholdersService

    @BeforeEach
    fun prepareFunnyGuilds() {
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(funnyGuilds)
        mockedFunnyGuilds.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(funnyGuildsLogger)

        lenient().`when`(funnyGuilds.pluginConfiguration).thenReturn(config)
        lenient().`when`(funnyGuilds.messageConfiguration).thenReturn(messages)
        lenient().`when`(funnyGuilds.tablistConfiguration).thenReturn(tablistConfig)

        userManager = UserManager()
        guildManager = GuildManager(config)
        userRankManager = UserRankManager(config)
        userRankManager.register(DefaultTops.defaultUserTops(config, userManager))
        guildRankManager = GuildRankManager(config)
        guildRankManager.register(DefaultTops.defaultGuildTops(guildManager))
        regionManager = RegionManager(config)

        lenient().`when`(funnyGuilds.userManager).thenReturn(userManager)
        lenient().`when`(funnyGuilds.guildManager).thenReturn(guildManager)
        lenient().`when`(funnyGuilds.userRankManager).thenReturn(userRankManager)
        lenient().`when`(funnyGuilds.guildRankManager).thenReturn(guildRankManager)
        lenient().`when`(funnyGuilds.regionManager).thenReturn(regionManager)

        rankPlaceholdersService = RankPlaceholdersService(funnyGuildsLogger, config, messages, tablistConfig, userRankManager, guildRankManager)

        lenient().`when`(funnyGuilds.rankPlaceholdersService).thenReturn(rankPlaceholdersService)
    }

    @AfterEach
    fun closeMockedFunnyGuilds() {
        mockedFunnyGuilds.close()
    }

}