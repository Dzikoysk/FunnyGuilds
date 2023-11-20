package net.dzikoysk.funnyguilds.feature.command.config;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import net.dzikoysk.funnyguilds.feature.command.admin.AddCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.AssistsCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.BanCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.BaseAdminCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.DeathsCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.DeputyAdminCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.FunnyGuildsCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.GuildsEnabledCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.KickAdminCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.LeaderAdminCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.LivesCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.LogoutsCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.MainCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.MoveCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.NameCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.PointsCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.ProtectionCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.SpyCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.TagCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.TeleportCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.UnbanCommand;
import net.dzikoysk.funnyguilds.feature.command.admin.ValidityAdminCommand;
import net.dzikoysk.funnyguilds.feature.command.user.AllyCommand;
import net.dzikoysk.funnyguilds.feature.command.user.BaseCommand;
import net.dzikoysk.funnyguilds.feature.command.user.BreakCommand;
import net.dzikoysk.funnyguilds.feature.command.user.ConfirmCommand;
import net.dzikoysk.funnyguilds.feature.command.user.CreateCommand;
import net.dzikoysk.funnyguilds.feature.command.user.DeleteCommand;
import net.dzikoysk.funnyguilds.feature.command.user.DeputyCommand;
import net.dzikoysk.funnyguilds.feature.command.user.EnlargeCommand;
import net.dzikoysk.funnyguilds.feature.command.user.EscapeCommand;
import net.dzikoysk.funnyguilds.feature.command.user.GuildCommand;
import net.dzikoysk.funnyguilds.feature.command.user.GuildInfoCommand;
import net.dzikoysk.funnyguilds.feature.command.user.HelpRequestCommand;
import net.dzikoysk.funnyguilds.feature.command.user.InviteCommand;
import net.dzikoysk.funnyguilds.feature.command.user.ItemsCommand;
import net.dzikoysk.funnyguilds.feature.command.user.JoinCommand;
import net.dzikoysk.funnyguilds.feature.command.user.KickCommand;
import net.dzikoysk.funnyguilds.feature.command.user.LeaderCommand;
import net.dzikoysk.funnyguilds.feature.command.user.LeaveCommand;
import net.dzikoysk.funnyguilds.feature.command.user.PlayerInfoCommand;
import net.dzikoysk.funnyguilds.feature.command.user.PvPCommand;
import net.dzikoysk.funnyguilds.feature.command.user.RankResetCommand;
import net.dzikoysk.funnyguilds.feature.command.user.SetBaseCommand;
import net.dzikoysk.funnyguilds.feature.command.user.TntCommand;
import net.dzikoysk.funnyguilds.feature.command.user.TopPlayerCommand;
import net.dzikoysk.funnyguilds.feature.command.user.ValidityCommand;
import net.dzikoysk.funnyguilds.feature.command.user.WarCommand;

@Header("Konfiguracja nazw komend i ich aliasów")
public class CommandConfiguration extends ConfigSection {

    @Comment
    @Comment("Komendy użytkownika")
    public UserCommands user = new UserCommands();

    public static class UserCommands extends ConfigSection {

        public General general = new General();

        public static class General extends ConfigSection {

            @Comment
            @CommandRegister(key = "tnt", as = TntCommand.class)
            public FunnyCommand tnt = new FunnyCommand("tnt");

            @Comment
            @CommandRegister(key = "top.player", as = TopPlayerCommand.class)
            public FunnyCommand topPlayer = new FunnyCommand("top gracze");

            @Comment
            @CommandRegister(key = "top.guild", as = TopPlayerCommand.class)
            public FunnyCommand topGuild = new FunnyCommand("top gildie");

        }

        public Player player = new Player();

        public static class Player extends ConfigSection {

            @Comment
            @CommandRegister(key = "info", as = PlayerInfoCommand.class)
            public FunnyCommand info = new FunnyCommand("gracz");

            @Comment
            @CommandRegister(key = "rank-reset", as = RankResetCommand.class)
            public FunnyCommand rankReset = new FunnyCommand("reset ranking");

            @Comment
            @CommandRegister(key = "stats-reset", as = RankResetCommand.class)
            public FunnyCommand statsReset = new FunnyCommand("reset statystyki");

        }

        public Guild guild = new Guild();

        public static class Guild extends ConfigSection {

            @Comment
            @CommandRegister(key = "help", as = GuildCommand.class)
            public FunnyCommand help = new FunnyCommand("gildia", Collections.singletonList("g"));

            @Comment
            @CommandRegister(key = "info", as = GuildInfoCommand.class)
            public FunnyCommand info = new FunnyCommand("gildia info");

            @Comment
            @CommandRegister(key = "items", as = ItemsCommand.class)
            public FunnyCommand items = new FunnyCommand("gildia przedmioty");

            @Comment
            @CommandRegister(key = "create", as = CreateCommand.class)
            public FunnyCommand create = new FunnyCommand("gildia zaloz");

            @Comment
            @CommandRegister(key = "delete", as = DeleteCommand.class)
            public FunnyCommand delete = new FunnyCommand("gildia usun");

            @Comment
            @CommandRegister(key = "enlarge", as = EnlargeCommand.class)
            public FunnyCommand enlarge = new FunnyCommand("gildia powieksz");

            @Comment
            @CommandRegister(key = "validity", as = ValidityCommand.class)
            public FunnyCommand validity = new FunnyCommand("gildia przedluz");

            @Comment
            @CommandRegister(key = "invite", as = InviteCommand.class)
            public FunnyCommand invite = new FunnyCommand("gildia zapros");

            @Comment
            @CommandRegister(key = "join", as = JoinCommand.class)
            public FunnyCommand join = new FunnyCommand("gildia dolacz");

            @Comment
            @CommandRegister(key = "leave", as = LeaveCommand.class)
            public FunnyCommand leave = new FunnyCommand("gildia opusc");

            @Comment
            @CommandRegister(key = "kick", as = KickCommand.class)
            public FunnyCommand kick = new FunnyCommand("gildia wyrzuc");

            @Comment
            @CommandRegister(key = "leader", as = LeaderCommand.class)
            public FunnyCommand leader = new FunnyCommand("gildia lider");

            @Comment
            @CommandRegister(key = "deputy", as = DeputyCommand.class)
            public FunnyCommand deputy = new FunnyCommand("gildia zastepca");

            @Comment
            @CommandRegister(key = "confirm", as = ConfirmCommand.class)
            public FunnyCommand confirm = new FunnyCommand("gildia potwierdz");

            @Comment
            @CommandRegister(key = "ally", as = AllyCommand.class)
            public FunnyCommand ally = new FunnyCommand("gildia sojusz");

            @Comment
            @CustomKey("break")
            @CommandRegister(key = "break-ally", as = BreakCommand.class)
            public FunnyCommand breakAlly = new FunnyCommand("gildia rozwiaz");

            @Comment
            @CommandRegister(key = "war", as = WarCommand.class)
            public FunnyCommand war = new FunnyCommand("gildia wojna");

            @Comment
            @CommandRegister(key = "pvp", as = PvPCommand.class)
            public FunnyCommand pvp = new FunnyCommand("gildia pvp");

            @Comment
            @CommandRegister(key = "setBase", as = SetBaseCommand.class)
            public FunnyCommand setBase = new FunnyCommand("gildia ustawbaze");

            @Comment
            @CommandRegister(key = "base", as = BaseCommand.class)
            public FunnyCommand base = new FunnyCommand("gildia baza");

            @Comment
            @CommandRegister(key = "help-request", as = HelpRequestCommand.class)
            public FunnyCommand helpRequest = new FunnyCommand("helprequest", Collections.singletonList("pp"));

            @Comment
            @CommandRegister(key = "escape", as = EscapeCommand.class)
            public FunnyCommand escape = new FunnyCommand("gildia ucieczka");

        }

    }

    @Comment
    @Comment("Komendy administratora")
    public AdminCommands admin = new AdminCommands();

    public static class AdminCommands extends ConfigSection {

        @CommandRegister(key = "funnyguilds", as = FunnyGuildsCommand.class)
        public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

        @Comment
        @CommandRegister(key = "main", as = MainCommand.class)
        public FunnyCommand help = new FunnyCommand("ga");

        @Comment
        @CommandRegister(key = "points", as = PointsCommand.class)
        public FunnyCommand points = new FunnyCommand("ga points");

        @Comment
        @CommandRegister(key = "kills", as = KickCommand.class)
        public FunnyCommand kills = new FunnyCommand("ga kills");

        @Comment
        @CommandRegister(key = "deaths", as = DeathsCommand.class)
        public FunnyCommand deaths = new FunnyCommand("ga deaths");

        @Comment
        @CommandRegister(key = "assists", as = AssistsCommand.class)
        public FunnyCommand assists = new FunnyCommand("ga assists");

        @Comment
        @CommandRegister(key = "logouts", as = LogoutsCommand.class)
        public FunnyCommand logouts = new FunnyCommand("ga logouts");

        @Comment
        @CommandRegister(key = "status", as = GuildsEnabledCommand.class)
        public FunnyCommand status = new FunnyCommand("ga status");

        @Comment
        @CommandRegister(key = "delete", as = DeleteCommand.class)
        public FunnyCommand delete = new FunnyCommand("ga usun");

        @Comment
        @CommandRegister(key = "tag", as = TagCommand.class)
        public FunnyCommand tag = new FunnyCommand("ga tag");

        @Comment
        @CommandRegister(key = "name", as = NameCommand.class)
        public FunnyCommand name = new FunnyCommand("ga nazwa");

        @Comment
        @CommandRegister(key = "validity", as = ValidityAdminCommand.class)
        public FunnyCommand validity = new FunnyCommand("ga przedluz");

        @Comment
        @CommandRegister(key = "protection", as = ProtectionCommand.class)
        public FunnyCommand protection = new FunnyCommand("ga ochrona");

        @Comment
        @CommandRegister(key = "lives", as = LivesCommand.class)
        public FunnyCommand lives = new FunnyCommand("ga zycia");

        @Comment
        @CommandRegister(key = "move", as = MoveCommand.class)
        public FunnyCommand move = new FunnyCommand("ga przenies");

        @Comment
        @CommandRegister(key = "teleport", as = TeleportCommand.class)
        public FunnyCommand teleport = new FunnyCommand("ga tp");

        @Comment
        @CommandRegister(key = "base", as = BaseAdminCommand.class)
        public FunnyCommand base = new FunnyCommand("ga baza");

        @Comment
        @CommandRegister(key = "add", as = AddCommand.class)
        public FunnyCommand add = new FunnyCommand("ga dodaj");

        @Comment
        @CommandRegister(key = "kick", as = KickAdminCommand.class)
        public FunnyCommand kick = new FunnyCommand("ga wyrzuc");

        @Comment
        @CommandRegister(key = "leader", as = LeaderAdminCommand.class)
        public FunnyCommand leader = new FunnyCommand("ga lider");

        @Comment
        @CommandRegister(key = "deputy", as = DeputyAdminCommand.class)
        public FunnyCommand deputy = new FunnyCommand("ga zastepca");

        @Comment
        @CommandRegister(key = "ban", as = BanCommand.class)
        public FunnyCommand ban = new FunnyCommand("ga ban");

        @Comment
        @CommandRegister(key = "unban", as = UnbanCommand.class)
        public FunnyCommand unban = new FunnyCommand("ga unban");

        @Comment
        @CommandRegister(key = "spy", as = SpyCommand.class)
        public FunnyCommand spy = new FunnyCommand("ga spy");

    }

    public static class FunnyCommand extends ConfigSection {

        public String name;
        public List<String> aliases;
        public boolean enabled;

        public FunnyCommand() {
        }

        public FunnyCommand(String name) {
            this(name, Collections.emptyList(), true);
        }

        public FunnyCommand(String name, List<String> aliases) {
            this(name, aliases, true);
        }

        public FunnyCommand(String name, List<String> aliases, boolean enabled) {
            this.name = name;
            this.aliases = aliases;
            this.enabled = enabled;
        }

    }

}
