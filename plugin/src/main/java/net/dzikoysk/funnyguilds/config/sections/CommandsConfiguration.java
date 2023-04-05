package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandsConfiguration extends OkaeriConfig {

    public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

    @Comment("")
    public FunnyCommand guild = new FunnyCommand("gildia", Arrays.asList("gildie", "g"));
    @Comment("")
    public FunnyCommand create = new FunnyCommand("zaloz");
    @Comment("")
    public FunnyCommand delete = new FunnyCommand("usun");
    @Comment("")
    public FunnyCommand confirm = new FunnyCommand("potwierdz");
    @Comment("")
    public FunnyCommand invite = new FunnyCommand("zapros");
    @Comment("")
    public FunnyCommand join = new FunnyCommand("dolacz");
    @Comment("")
    public FunnyCommand leave = new FunnyCommand("opusc");
    @Comment("")
    public FunnyCommand kick = new FunnyCommand("wyrzuc");
    @Comment("")
    public FunnyCommand base = new FunnyCommand("baza");
    @Comment("")
    public FunnyCommand enlarge = new FunnyCommand("powieksz");
    @Comment("")
    public FunnyCommand ally = new FunnyCommand("sojusz");
    @Comment("")
    public FunnyCommand war = new FunnyCommand("wojna");
    @Comment("")
    public FunnyCommand items = new FunnyCommand("przedmioty");
    @Comment("")
    public FunnyCommand escape = new FunnyCommand("ucieczka", Collections.singletonList("escape"));
    @Comment("")
    public FunnyCommand rankReset = new FunnyCommand("rankreset", Collections.singletonList("resetrank"));
    @Comment("")
    public FunnyCommand statsReset = new FunnyCommand("statsreset", Collections.singletonList("resetstats"));
    @Comment("")
    public FunnyCommand tnt = new FunnyCommand("tnt");
    @Comment("")
    @CustomKey("break")
    public FunnyCommand break_ = new FunnyCommand("rozwiaz");
    @Comment("")
    public FunnyCommand info = new FunnyCommand("info");
    @Comment("")
    public FunnyCommand player = new FunnyCommand("gracz");
    @Comment("")
    public FunnyCommand top = new FunnyCommand("top", Collections.singletonList("top10"));
    @Comment("")
    public FunnyCommand validity = new FunnyCommand("przedluz");
    @Comment("")
    public FunnyCommand leader = new FunnyCommand("lider", Collections.singletonList("zalozyciel"));
    @Comment("")
    public FunnyCommand deputy = new FunnyCommand("zastepca");
    @Comment("")
    public FunnyCommand ranking = new FunnyCommand("ranking");
    @Comment("")
    public FunnyCommand setbase = new FunnyCommand("ustawbaze", Collections.singletonList("ustawdom"));
    @Comment("")
    public FunnyCommand pvp = new FunnyCommand("pvp", Collections.singletonList("ustawpvp"));
    @Comment("")
    public FunnyCommand helpRequest = new FunnyCommand("helprequest", Collections.singletonList("pp"));

    @Comment
    @Comment("Komendy administratora")
    public AdminCommands admin = new AdminCommands();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class FunnyCommand extends OkaeriConfig {

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

    @Names(strategy = NameStrategy.IDENTITY)
    public static class AdminCommands extends OkaeriConfig {

        public String main = "ga";
        public String points = "ga points";
        public String kills = "ga kills";
        public String deaths = "ga deaths";
        public String assists = "ga assists";
        public String logouts = "ga logouts";
        public String status = "ga status";
        public String delete = "ga usun";
        public String tag = "ga tag";
        public String name = "ga nazwa";
        public String validity = "ga przedluz";
        public String protection = "ga ochrona";
        public String lives = "ga zycia";
        public String move = "ga przenies";
        public String teleport = "ga tp";
        public String base = "ga baza";
        public String add = "ga dodaj";
        public String kick = "ga wyrzuc";
        public String leader = "ga lider";
        public String deputy = "ga zastepca";
        public String ban = "ga ban";
        public String unban = "ga unban";
        public String spy = "ga spy";

    }

}
