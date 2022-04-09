package net.dzikoysk.funnyguilds

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.config.MessageConfiguration
import net.dzikoysk.funnyguilds.config.NumberRange
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.guild.GuildRankManager
import net.dzikoysk.funnyguilds.guild.RegionManager
import net.dzikoysk.funnyguilds.rank.DefaultTops
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserManager
import net.dzikoysk.funnyguilds.user.UserRankManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.lenient
import static org.mockito.Mockito.mockStatic

@CompileStatic
@ExtendWith(MockitoExtension.class)
class FunnyGuildsSpec extends BukkitSpec {

    protected static MockedStatic<FunnyGuilds> mockedFunnyGuilds
    protected static MockedStatic<BossBarProvider> mockedBossBarProvider

    @Mock
    public FunnyGuilds funnyGuilds

    protected PluginConfiguration config = new PluginConfiguration()
    protected MessageConfiguration messages = new MessageConfiguration()
    protected TablistConfiguration tablistConfig = new TablistConfiguration()

    protected UserManager userManager
    protected GuildManager guildManager
    protected UserRankManager userRankManager
    protected GuildRankManager guildRankManager
    protected RegionManager regionManager

    protected RankPlaceholdersService rankPlaceholdersService

    @BeforeAll
    static void openMockedFunnyGuilds() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds.class)
        mockedBossBarProvider = mockStatic(BossBarProvider.class)
    }

    @BeforeEach
    void prepareFunnyGuilds() {
        lenient().when(funnyGuilds.getPluginConfiguration()).thenReturn(config)
        lenient().when(funnyGuilds.getMessageConfiguration()).thenReturn(messages)
        lenient().when(funnyGuilds.getTablistConfiguration()).thenReturn(tablistConfig)

        userManager = new UserManager()
        guildManager = new GuildManager(config);
        userRankManager = new UserRankManager(config);
        userRankManager.register(DefaultTops.defaultUserTops(config, userManager))
        guildRankManager = new GuildRankManager(config);
        guildRankManager.register(DefaultTops.defaultGuildTops(guildManager))
        regionManager = new RegionManager(config);

        lenient().when(funnyGuilds.getUserManager()).thenReturn(userManager)
        lenient().when(funnyGuilds.getGuildManager()).thenReturn(guildManager)
        lenient().when(funnyGuilds.getUserRankManager()).thenReturn(userRankManager)
        lenient().when(funnyGuilds.getGuildRankManager()).thenReturn(guildRankManager)
        lenient().when(funnyGuilds.getRegionManager()).thenReturn(regionManager)

        rankPlaceholdersService = new RankPlaceholdersService(null, config, messages, tablistConfig, userRankManager, guildRankManager)

        lenient().when(funnyGuilds.getRankPlaceholdersService()).thenReturn(rankPlaceholdersService)

        mockedFunnyGuilds.when({ FunnyGuilds.getInstance() }).thenReturn(funnyGuilds)
        mockedBossBarProvider.when(() -> BossBarProvider.getBossBar(any(User.class))).thenReturn(null)
    }

    @BeforeEach
    void preparePluginConfiguration() {
        Map<NumberRange, Integer> parsedData = new HashMap<>()

        NumberRange.parseIntegerRange(config.eloConstants_, false)
                .forEach((range, number) -> parsedData.put(range, Integer.parseInt(number)))

        config.eloConstants = parsedData
    }

    @AfterAll
    static void closeMockedFunnyGuilds() {
        mockedFunnyGuilds.close()
        mockedBossBarProvider.close()
    }

}
