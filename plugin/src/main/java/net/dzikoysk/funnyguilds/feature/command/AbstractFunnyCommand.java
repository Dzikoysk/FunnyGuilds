package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import org.bukkit.Server;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyCommand {

    @Inject
    public FunnyGuilds plugin;
    @Inject
    public FunnyGuildsLogger logger;
    @Inject
    public Server server;
    @Inject
    public FunnyServer funnyServer;

    @Inject
    public PluginConfiguration config;
    @Inject
    public TablistConfiguration tablistConfig;

    @Inject
    public MessageService messageService;

    @Inject
    public UserManager userManager;
    @Inject
    public GuildManager guildManager;
    @Inject
    public UserRankManager userRankManager;
    @Inject
    public GuildRankManager guildRankManager;
    @Inject
    public RegionManager regionManager;

    @Inject
    public BasicPlaceholdersService basicPlaceholdersService;
    @Inject
    public TimePlaceholdersService timePlaceholdersService;
    @Inject
    public UserPlaceholdersService userPlaceholdersService;
    @Inject
    public GuildPlaceholdersService guildPlaceholdersService;
    @Inject
    public RankPlaceholdersService rankPlaceholdersService;

}
