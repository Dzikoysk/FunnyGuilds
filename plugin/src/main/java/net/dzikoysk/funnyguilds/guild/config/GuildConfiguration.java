package net.dzikoysk.funnyguilds.guild.config;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.NotBlank;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import net.dzikoysk.funnyguilds.config.AutoColor;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import net.dzikoysk.funnyguilds.config.DefaultRegex;
import net.dzikoysk.funnyguilds.config.FunnyPattern;
import net.dzikoysk.funnyguilds.config.requirement.PriorityRequirementsComponent;
import net.dzikoysk.funnyguilds.config.requirement.RankRequirementsComponent;
import net.dzikoysk.funnyguilds.config.StringValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerStorage;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import org.jetbrains.annotations.Nullable;

public class GuildConfiguration extends ConfigSection {

    @Comment("Czy ma być włączona możliwość zakładania gildii (można ją zmienić także za pomocą komendy /ga enabled)")
    public boolean enabled = true;

    public Validation validation = new Validation();

    public static class Validation extends ConfigSection {

        public Name name = new Name();

        public static class Name extends ConfigSection implements StringValidation {

            @Min(1)
            @Comment
            @Comment("Maksymalna długość nazwy gildii")
            public int minLength = 4;

            @Min(1)
            @Comment
            @Comment("Minimalna długość nazwy gildii")
            public int maxLength = 22;

            @Comment
            @Comment("Konfiguracja wyrażenia regularnego, które musi spełniać nazwa gildii")
            @Comment("Jak skonfigurować tą wartość znajdziesz tutaj: https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#regex")
            public FunnyPattern regex = new FunnyPattern(DefaultRegex.LETTERS);

            @Override
            public int getMinLength() {
                return this.minLength;
            }

            @Override
            public int getMaxLength() {
                return this.maxLength;
            }

            @Override
            public FunnyPattern getRegex() {
                return this.regex;
            }

        }

        @Comment
        public Tag tag = new Tag();

        public static class Tag extends ConfigSection implements StringValidation {

            @Min(1)
            @Comment("Maksymalna długość tagu gildii")
            public int minLength = 3;

            @Min(1)
            @Comment
            @Comment("Minimalna długość tagu gildii")
            public int maxLength = 5;

            @Comment
            @Comment("Konfiguracja wyrażenia regularnego, które musi spełniać tag gildii")
            @Comment("Jak skonfigurować tą wartość znajdziesz tutaj: https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#regex")
            public FunnyPattern regex = new FunnyPattern(DefaultRegex.LETTERS);

            @Override
            public int getMinLength() {
                return this.minLength;
            }

            @Override
            public int getMaxLength() {
                return this.maxLength;
            }

            @Override
            public FunnyPattern getRegex() {
                return this.regex;
            }

        }

    }

    @Comment
    public Create create = new Create();

    public static class Create extends ConfigSection {

        @Comment("Światy w których zablokowane jest zakładanie gildii")
        public Set<String> blockedWorlds = Set.of("world_nether", "world_the_end");

        @Min(0)
        @Comment
        @Comment("Minimalna odległość w jakiej gildia może zostać założona od spawnu")
        public int distanceToSpawn = 100;

        @Min(0)
        @Comment
        @Comment("Minimalna odległość w jakiej gildia może zostać założona od granicy świata")
        public int distanceToBorder = 100;

        @Min(0)
        @Comment
        @Comment("Minimalna odległość w jakiej gildia może zostać założona od innej gildii")
        public int distanceToOtherGuild = 100;

        @Comment
        @Comment("Konfiguracja przedmiotów, doświadczenia, pieniędzy i punktów rankingowych potrzebnych do założenia gildii")
        @Comment("Format konfiguracji:")
        @Comment("<uprawnienie>:")
        @Comment("  <wymagania> (https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#requirements)")
        @Comment
        @Comment("Format finalnego uprawnienia jakie należy nadać graczu: funnyguilds.create.requirements.<uprawnienie>")
        @Comment("WAŻNE:")
        @Comment("- Każda konfiguracja powinna mieć inny priorytet inaczej jedna konfiguracja nadpisze druga")
        @Comment("- Wymagania z wyższym priorytetem będą wybierane jako pierwsze, jeśli gracz posiada odpowiednie uprawnienie")
        @Comment("- Jeśli gracz nie posiada żadnego z wymaganych uprawnień, to będzie mógł założyć gildię bez żadnych wymagań")
        public Map<String, RankRequirementsComponent> requirements = Map.of(
                "default", new RankRequirementsComponent(0),
                "vip", new RankRequirementsComponent(1),
                "admin", new RankRequirementsComponent(127)
        );

        @Override
        public void processProperties() {
            this.requirements = MapUtils.sortByValue(this.requirements, true);
        }

    }

    public Validity validity = new Validity();

    public static class Validity extends ConfigSection {

    }

    @Comment
    public Lives lives = new Lives();

    public static class Lives extends ConfigSection {

        @Comment("Liczba serc z jakimi gildia będzie utworzona")
        public int startLives = 3;

        @Comment
        public RepeatingSymbol repeatingSymbol = new RepeatingSymbol();

        public static class RepeatingSymbol extends ConfigSection {

            @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL lub LIVES-SYMBOL-ALL")
            @AutoColor
            public String full = "&c\u2764";

            @Comment("")
            @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL")
            @AutoColor
            public String empty = "&8\u2764";

            @Comment("")
            @Comment("Symbol (lub słowo), który ma być pokazywany na końcu placeholdera LIVES-SYMBOL, kiedy gildia posiada więcej żyć niz podstawowe (war-lives)")
            @AutoColor
            public String more = "&a+";

        }

    }

    @Comment
    public RegionConfiguration region = new RegionConfiguration();

    @Comment
    public RelationalTag relationalTag = new RelationalTag();

    public static class RelationalTag extends ConfigSection {

        @Comment("Wygląd tagu osób w tej samej gildii")
        @AutoColor
        public String our = "&a{TAG}&f";

        @Comment
        @Comment("Wygląd tagu gildii sojuszniczej")
        @AutoColor
        public String allies = "&6{TAG}&f";

        @Comment
        @Comment("Wygląd tagu wrogiej gildii")
        @AutoColor
        public String enemies = "&c{TAG}&f";

        @Comment
        @Comment("Wygląd tagu gildii neutralnej, widziany również przez graczy bez gildii")
        @AutoColor
        public String other = "&7{TAG}&f";

        public String chooseTag(@Nullable Guild guild, @Nullable Guild targetGuild) {
            if (targetGuild == null) {
                return "";
            }

            if (guild == null) {
                return this.other;
            }

            if (guild.equals(targetGuild)) {
                return this.our;
            }

            if (guild.isAlly(targetGuild) || targetGuild.isAlly(guild)) {
                return this.allies;
            }

            if (guild.isEnemy(targetGuild) || targetGuild.isEnemy(guild)) {
                return this.enemies;
            }

            return this.other;
        }

        public String chooseAndPrepareTag(@Nullable Guild guild, @Nullable Guild targetGuild) {
            if (targetGuild == null) {
                return "";
            }

            return FunnyFormatter.of("{TAG}", targetGuild.getTag())
                    .format(this.chooseTag(guild, targetGuild));
        }

    }

    @Min(1)
    @Comment
    @Comment("Maksymalna liczba członków gildii")
    public int maxMembers = 15;

    @Min(0)
    @Comment
    @Comment("Maksymalna liczba gildii z którymi można posiadać sojusz")
    public int maxAllies = 15;

    @Min(0)
    @Comment
    @Comment("Maksymalna liczba gildii z którymi można być w stanie wojny")
    public int maxEnemies = 15;

    @Comment
    @Comment("Czy gracz po śmierci ma się pojawiać w bazie swojej gildii")
    @Comment("Działa tylko jeśli regiony są włączone")
    public boolean respawnInBase = true;

    @Comment
    @Comment("Konfiguracja przedmiotów, doświadczenia, pieniędzy i punktów rankingowych potrzebnych do dołączenia do gildii")
    @Comment("Format konfiguracji:")
    @Comment("<uprawnienie>:")
    @Comment("  <wymagania> (https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#requirements)")
    @Comment
    @Comment("Format finalnego uprawnienia jakie należy nadać graczu: funnyguilds.join.requirements.<uprawnienie>")
    @Comment("WAŻNE:")
    @Comment("- Każda konfiguracja powinna mieć inny priorytet inaczej jedna konfiguracja nadpisze druga")
    @Comment("- Wymagania z wyższym priorytetem będą wybierane jako pierwsze, jeśli gracz posiada odpowiednie uprawnienie")
    @Comment("- Jeśli gracz nie posiada żadnego z wymaganych uprawnień, to będzie mógł dołączyć do gildii bez żadnych wymagań")
    public Map<String, RankRequirementsComponent> joinRequirements = Map.of(
            "default", new RankRequirementsComponent(0),
            "vip", new RankRequirementsComponent(1),
            "admin", new RankRequirementsComponent(127)
    );

    @Comment
    @Comment("Konfiguracja przedmiotów, doświadczenia, i pieniędzy potrzebnych do teleportacji do bazy gildii")
    @Comment("Format konfiguracji:")
    @Comment("<uprawnienie>:")
    @Comment("  <wymagania> (https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#requirements)")
    @Comment
    @Comment("Format finalnego uprawnienia jakie należy nadać graczu: funnyguilds.base.requirements.<uprawnienie>")
    @Comment("WAŻNE:")
    @Comment("- Każda konfiguracja powinna mieć inny priorytet inaczej jedna konfiguracja nadpisze druga")
    @Comment("- Wymagania z wyższym priorytetem będą wybierane jako pierwsze, jeśli gracz posiada odpowiednie uprawnienie")
    @Comment("- Jeśli gracz nie posiada żadnego z wymaganych uprawnień, to będzie mógł teleportować się do bazy gildii bez żadnych wymagań")
    public Map<String, PriorityRequirementsComponent> teleportBaseRequirements = Map.of(
            "default", new PriorityRequirementsComponent(0),
            "vip", new PriorityRequirementsComponent(1),
            "admin", new PriorityRequirementsComponent(127)
    );

    @Comment
    @Comment("Czasy oczekiwania na teleportację dla poszczególnych uprawnień")
    @Comment("Wartości powinny być podane od najwyższego czasu do najniższego")
    @Comment("Format dla wartości: <uprawnienie>: <czas>")
    @Comment
    @Comment("Format finalnego uprawnienia jakie należy nadać graczu: funnyguilds.base.delay.<uprawnienie>")
    @Comment("WAŻNE: Jeśli dany gracz nie będzie miał żadnego z podanych uprawnień to czas zawsze będzie wynosił 0 sekund!")
    @Comment
    @Comment("Format czasu: https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca")
    public Map<String, Duration> teleportBaseDelay = Map.of(
            "default", Duration.ofSeconds(5),
            "vip", Duration.ofSeconds(3),
            "admin", Duration.ZERO
    );

    @Comment
    public Chat chat = new Chat();

    public static class Chat extends ConfigSection {

        @NotBlank
        @Comment("Symbol, od którego zaczyna się wiadomość do gildii")
        public String privateSuffix = "!";

        @NotBlank
        @Comment
        @Comment("Symbol od którego zaczyna się wiadomość do sojuszników gildii")
        public String allySuffix = "!!";

        @NotBlank
        @Comment
        @Comment("Symbol od którego zaczyna się wiadomość do wszystkich gildii")
        public String globalSuffix = "!!!";

        @Comment
        @Comment("Czy wiadomości z chatów gildyjnych powinny być wyświetlane w logach serwera")
        public boolean logGuildChat = false;

        //TODO move to MesssageConfiguration
        @Comment("")
        @Comment("Wygląd wiadomości wysyłanej na czacie gildii")
        @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
        public String privDesign = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}";

        @Comment("")
        @Comment("Wygląd wiadomości wysyłanej na czacie dla sojuszników")
        @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
        public String allyDesign = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

        @Comment("")
        @Comment("Wygląd wiadomości wysyłanej na czacie globalnym gildii")
        @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
        public String globalDesign = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

        @Comment("")
        @Comment("Wygląd wiadomoci wysyłanej na czacie gildyjnym/sojuszniczym/globalnym gildii, dla osób z włączonym /ga spy")
        @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
        public String spyDesign = "&8[&6Spy&8] &7{PLAYER}&8:&f {MESSAGE}";

    }

    public MemberPosition memberPosition = new MemberPosition();

    public static class MemberPosition extends ConfigSection {

        @Comment("Znacznik dla lidera gildii")
        @AutoColor
        public String leader = "**";

        @Comment
        @Comment("Znacznik dla zastępcy gildii")
        @AutoColor
        public String deputy = "*";

        @Comment
        @Comment("Znacznik dla członka gildii")
        @AutoColor
        public String member = "";

    }

    @Comment
    public SecuritySystemConfiguration securitySystem = new SecuritySystemConfiguration();

    @Override
    public void processProperties() {
        this.joinRequirements = MapUtils.sortByValue(this.joinRequirements, true);
        this.teleportBaseRequirements = MapUtils.sortByValue(this.teleportBaseRequirements, true);
        DynamicListenerStorage.respawnEvent = this.respawnInBase;
    }

    public boolean isRegionsEnabled() {
        return this.enabled && this.region.enabled;
    }

}
