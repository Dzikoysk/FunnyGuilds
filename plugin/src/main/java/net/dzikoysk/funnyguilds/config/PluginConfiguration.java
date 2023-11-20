package net.dzikoysk.funnyguilds.config;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.DecimalMax;
import eu.okaeri.validator.annotation.DecimalMin;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.MysqlConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TntProtectionConfiguration;
import net.dzikoysk.funnyguilds.guild.config.HeartConfiguration;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerStorage;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.rank.config.TopConfiguration;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.LegacyUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("          FunnyGuilds           #")
@Header("         4.13.0 Snowdrop        #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderów znajduje się tutaj:")
@Header("https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-PlaceholderAPI")
@Header(" ")
@Header("Jeżeli chcesz, aby dana wiadomość była pusta, zamiast wiadomości umieść: ''")
public class PluginConfiguration extends ConfigSection {

    @Exclude
    public final Cooldown<UUID> informationMessageCooldowns = new Cooldown<>();

    @Comment("")
    @Comment("Wyświetlana nazwa pluginu")
    public String pluginName = "FunnyGuilds";

    @Comment("")
    @Comment("Czy plugin ma działać w trybie debug - służy on do wysyłania dodatkowych wiadomości, w celu diagnozowania błędów")
    public boolean debugMode = false;

    @Comment("")
    @Comment("Czy informacje o aktualizacji mają być widoczne podczas wejścia na serwer")
    public boolean updateInfo = true;

    @Comment("")
    @Comment("Czy informacje o aktualizacji wersji nightly mają być widoczne podczas wejścia na serwer")
    @Comment("Ta opcja działa tylko wtedy, gdy włączona jest opcja 'update-info'")
    public boolean updateNightlyInfo = true;

    @Comment("")
    @Comment("Domyślny używany język używany przez plugin jeżeli nie można znaleźć języka gracza")
    public Locale defaultLocale = Locale.forLanguageTag("pl");

    @Comment("")
    @Comment("Lista języków używanych przez plugin")
    @Comment("Jeżeli chcesz dodać nowy język dodaj go tutaj - utworzy to nowy plik z domyślnymi wartościami, które możesz później edytować")
    @Comment("Języki gracza są dobierane automatycznie na podstawie ustawiań klienta")
    public Set<Locale> availableLocales = new HashSet<>(Arrays.asList(Locale.forLanguageTag("pl"), Locale.forLanguageTag("en")));

    @Comment("")
    @Comment("Strefa czasowa używana przez plugin do wyświetlania dat (np. na tabliście)")
    @Comment("Możesz znaleźć listę stref czasowych tutaj: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones")
    public ZoneId timeZone = ZoneId.of("Europe/Warsaw");

    @Comment
    @Comment("Czy płyny mają się rozlewać tylko na terenie gildii")
    @Comment("Działa tylko jeśli regiony są włączone")
    public boolean fluidFlowOnlyOnRegions = false;

    @Comment
    @Comment("Czy wiadomości o braku potrzebnych przedmiotów maja zawierać elementy, na które można najechać")
    @Comment("Takie elementy pokazują informacje o przedmiocie, np. jego typ, nazwę czy opis")
    public boolean chatItemComponents = true;

    @Comment("")
    @Comment("Zasada sprawdzania nicków graczy")
    @Comment("Możliwe zasady są takie same jak w przypadku name-regex")
    public FunnyPattern playerNameRegex = new FunnyPattern(DefaultRegex.LETTERS_DIGITS_UNDERSCORE);

    @Comment("")
    @Comment("Minimalna długość nicku gracza")
    public int playerNameMinLength = 3;

    @Comment("")
    @Comment("Maksymalna długość nicku gracza")
    public int playerNameMaxLength = 16;

    @Min(0)
    @Comment("")
    @Comment("Minimalna liczba graczy w gildii, aby zaliczała się ona do rankingu")
    @CustomKey("guild-min-members")
    public int minMembersToInclude = 1;

    @Comment("")
    @Comment("Konfiguracja serca gildii")
    @CustomKey("heart-configuration")
    public HeartConfiguration heart = new HeartConfiguration();

    @Comment("")
    @Comment("Możliwość ucieczki z terenu innej gildii")
    @Comment("Funkcja niedostępna, jeśli możliwość teleportacji do gildii jest wyłączona")
    public boolean escapeEnable = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas jaki musi upłynąć od włączenia ucieczki do teleportacji")
    @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
    @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
    @Comment("Przykład: 1m30s")
    public Duration escapeDelay = Duration.ofMinutes(2);

    @Comment("")
    @Comment("O ile powiększany jest teren gildii przy zwiększeniu poziomu")
    public int enlargeSize = 5;

    @Min(1)
    @Comment("")
    @Comment("Wielkość regionu gildii")
    public int regionSize = 50;

    @Min(0)
    @Comment("")
    @Comment("Minimalna odległość między terenami gildii")
    public int regionMinDistance = 10;

    @Min(1)
    @Comment("")
    @Comment("Co ile może byc wywoływany pasek powiadomień przez jednego gracza, w sekundach")
    public int regionNotificationCooldown = 60;

    @Comment("")
    @Comment("Czy proces usunięcia gildii powinien zostać przerwany, jezeli ktoś spoza gildii jest na jej terenie")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas przez jaki nie można budować na terenie gildii po wybuchu")
    @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
    @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
    @Comment("Przykład: 1m30s")
    public Duration regionExplode = Duration.ofMinutes(2);

    @Comment("")
    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli gildia jest chroniona")
    public boolean regionExplodeBlockProtected = false;

    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli TNT jest wyłączone")
    public boolean regionExplodeBlockTntDisabled = false;

    @Comment("")
    @Comment("Lista entity, których wybuch nie powoduje blokady budowania na terenie gildii")
    @Comment("Nazwy entity muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/entity/EntityType.html (nie wszystkie entity wybuchają)")
    public Set<EntityType> regionExplodeExcludeEntities = EntityUtils.parseEntityTypes(true, "CREEPER", "WITHER", "WITHER_SKULL", "FIREBALL");

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież niszczenie bloków")
    public boolean regionExplodeBlockBreaking = false;

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież interakcje z blocked-interact")
    public boolean regionExplodeBlockInteractions = false;

    @Min(0)
    @Comment("")
    @Comment("Zasięg pobieranych przedmiotów po wybuchu, jeżeli chcesz wyłączyć - wpisz 0")
    public int explodeRadius = 3;

    @Comment("")
    @Comment("Jakie materiały, i z jaka szansą, maja byc niszczone po wybuchu")
    @Comment("<material>: <szansa (w %)>")
    @Comment("Jeżeli wszystkie materiały mają mieć określony % na wybuch - uzyj specjalnego znaku '*'")
    @CustomKey("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of(
            "ender_chest", 20.0,
            "enchanting_table", 20.0,
            "obsidian", 20.0,
            "water", 33.0,
            "lava", 33.0
    );

    @Exclude
    public Map<Material, Double> explodeMaterials;
    @Exclude
    public boolean allMaterialsAreExplosive;
    @Exclude
    public double defaultExplodeChance = -1.0;

    @Comment
    @Comment("Czy powstałe wybuchy powinny niszczyć bloki wyłącznie na terenach gildii")
    public boolean explodeShouldAffectOnlyGuild = false;

    @Comment
    public TntProtectionConfiguration tntProtection = new TntProtectionConfiguration();

    @Comment("")
    @Comment("Możliwość podbijania gildii")
    public boolean warEnabled = true;

    @Min(1)
    @Comment("")
    @Comment("Ile żyć ma gildia")
    public int warLives = 3;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Po jakim czasie od założenia można zaatakować gildię")
    @CustomKey("war-protection")
    public Duration warProtection = Duration.ofHours(24);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Ile czasu trzeba czekać do następnego ataku na gildię")
    @CustomKey("war-wait")
    public Duration warWait = Duration.ofHours(24);

    @Comment("")
    @Comment("Czy gildia podczas okresu ochronnego ma posiadać ochronę przeciw TNT")
    public boolean warTntProtection = true;

    @Comment("")
    @Comment("Czy zwierzęta na terenie gildii mają być chronione przed osobami spoza gildii")
    public boolean animalsProtection = false;

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Jaką ważność ma gildia po jej założeniu")
    @CustomKey("validity-start")
    public Duration validityStart = Duration.ofDays(14);

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile czasu dodaje przedłużenie ważności gildii")
    @CustomKey("validity-time")
    public Duration validityTime = Duration.ofDays(14);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile dni przed końcem wygasania można przedłużyć gildię, wpisz 0 jeżeli funkcja ma byc wylaczona")
    @CustomKey("validity-when")
    public Duration validityWhen = Duration.ofDays(14);

    @Comment("")
    @Comment("Koszt przedłużenia gildii")
    public List<ItemStack> validityItems = ItemUtils.parseItems("10 diamond");

    @Comment("")
    @Comment("Komu ma być wyświetlana wiadomość o śmierci gracza")
    @Comment("PARTICIPANTS - tylko uczestnikom walki")
    @Comment("GUILD - wszystkim uczestnikom walki i wszystkim członkom gildii ofiary, zabójcy i asystujących")
    @Comment("WORLD - wszystkim uczestnikom walki i wszystkim graczom w świecie w którym umarła ofiara")
    @Comment("ALL - wszystkim uczestnikom walki i wszystkim graczom na serwerze")
    public DeathMessageReceivers deathMessageReceivers = DeathMessageReceivers.ALL;

    public enum DeathMessageReceivers {

        PARTICIPANTS,
        GUILD,
        WORLD,
        ALL

    }

    @Comment("")
    @Comment("Czy wyłączyć wyświetlanie domyślnej wiadomości o śmierci gracza")
    public boolean disableDefaultDeathMessage = true;

    @Comment("")
    @Comment("Ranking, od którego rozpoczyna gracz")
    public int rankStart = 1000;

    @Comment("")
    @Comment("Czy blokada nabijania rankingu na tych samych osobach powinna byc włączona")
    public boolean rankFarmingProtect = true;

    @Comment("")
    @Comment("Czy opcja blokady nabijania rankingu powinna działać w obie strony tzn. jeśli gracz nas zabije, a potem zabijemy go my to nie dostaniemy punktów")
    public boolean bidirectionalRankFarmingProtect = false;

    @Comment("")
    @Comment("Czy ostatnia osoba, która zaatakowała gracza, który zginął, ma być uznawana za jego zabójcę")
    @CustomKey("rank-farming-last-attacker-as-killer")
    public boolean considerLastAttackerAsKiller = false;

    public DamageTracking damageTracking = new DamageTracking();

    public static class DamageTracking extends ConfigSection {

        @Comment("Czas po którym zadane obrażenia, stają się \"przestarzałe\"")
        public Duration expireTime = Duration.ofMinutes(1);

        @Min(-1)
        @Comment("Jak długa ma być historia zadanych obrażeń.")
        @Comment("Wstaw -1 jeśli ma być nieskończona.")
        public int maxTracks = 30;

    }

    @PositiveOrZero
    @Comment("")
    @Comment("Czas przez jaki osoba, która zaatakowała gracza, który zginął, ma być uznawany za jego zabójcę")
    @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
    @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
    @Comment("Przykład: 1m30s")
    @CustomKey("rank-farming-consideration-timeout")
    public Duration lastAttackerAsKillerConsiderationTimeout = Duration.ofSeconds(30);

    @PositiveOrZero
    @Comment("")
    @Comment("Czas trwania blokady nabijania rankingu po walce dwóch osób")
    public Duration rankFarmingCooldown = Duration.ofHours(2);

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki mają taki sam adres IP")
    public boolean rankIPProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są członkami tej samej gildii")
    public boolean rankMemberProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są z sojuszniczych gildii")
    public boolean rankAllyProtect = false;

    @Comment("")
    @Comment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzględnieni przy wyznaczaniu pozycji gracza w rankingu")
    @CustomKey("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    @Min(1)
    @Comment("")
    @Comment("Co ile ticków ranking graczy oraz gildii powinien być odświeżany (20 ticków = 1 sekunda)")
    public int rankingUpdateInterval = 40;

    @Comment("")
    @Comment("Czy system asyst ma byc włączony")
    @CustomKey("rank-assist-enable")
    public boolean assistEnable = true;

    @Min(-1)
    @Comment("")
    @Comment("Limit asyst, wpisz liczbę ujemną aby wyłączyć")
    @CustomKey("assists-limit")
    public int assistsLimit = -1;

    @DecimalMin("0")
    @DecimalMax("1")
    @Comment("")
    @Comment("Jaka część rankingu za zabicie idzie na konto zabójcy")
    @Comment("1 to cały ranking, 0 to nic")
    @Comment("Reszta rankingu rozdzielana jest między osoby asystujące, w zaleznosci od zadanych obrażeń")
    @CustomKey("rank-assist-killer-share")
    public double assistKillerShare = 0.5;

    @Comment("")
    @Comment("Czy zabójcy zawsze mają dzielić sie ilością punktów według rank-assist-killer-share, nawet gdy nie ma osób asystujących")
    @CustomKey("rank-assist-victim-always-share")
    public boolean assistKillerAlwaysShare = false;

    @Comment("")
    @Comment("Na jakich regionach ma być ignorowane nadawanie asyst")
    @Comment("UWAGA: wymagany plugin WorldGuard")
    @Comment("Zamiast tej opcji w configu, zalecamy ustawienie flagi 'fg-no-assists' na regionach, na których asysty nie powinny być naliczane")
    @Comment("Ta opcja konfiguracji zniknie z configu w przyszłych wydaniach i nie powinna być używana")
    public Set<String> assistsRegionsIgnored = Collections.emptySet(); //TODO [5.0]: Remove

    @Comment("")
    @Comment("System rankingowy używany przez plugin, do wyboru:")
    @Comment(" ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @Comment(" PERCENT - system, który obu graczom zabiera procent rankingu osoby zabitej")
    @Comment(" STATIC - system, który zawsze zabiera x rankingu zabijającemu i x zabitemu")
    @CustomKey("rank-system")
    public RankSystem.Type rankSystem = RankSystem.Type.ELO;

    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Lista stałych do obliczeń rankingowych ELO - im mniejsza stała, tym mniejsze zmiany rankingu")
    @Comment("Stałe określają też o ile maksymalnie może zmienić się ranking pochodzący z danego przedziału")
    @Comment("Lista powinna być podana od najmniejszych do największych rankingów i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minRank-maxRank stała\", np.: \"0-1999 32\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w gore, np.: \"2401-* 16\"")
    @CustomKey("elo-constants")
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @Exclude
    public Map<NumberRange, Integer> eloConstants;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Dzielnik obliczeń rankingowych ELO - im mniejszy dzielnik, tym większe zmiany rankingu")
    @Comment("Dzielnik powinien być liczbą dodatnią, niezerową")
    @CustomKey("elo-divider")
    public double eloDivider = 400.0D;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Wykładnik potęgi obliczeń rankingowych ELO - im mniejszy wykładnik, tym wieksze zmiany rankingu")
    @Comment("Wykładnik powinien być liczbą dodatnią, niezerową")
    @CustomKey("elo-exponent")
    public double eloExponent = 10.0D;

    @DecimalMin("0")
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest PERCENT!")
    @Comment("Procent rankingu osoby zabitej, o jaki zmienią się rankingi po walce")
    @CustomKey("percent-rank-change")
    public double percentRankChange = 1.0;

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty dawane osobie, która wygrała walkę")
    @CustomKey("static-attacker-change")
    public int staticAttackerChange = 15;

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty zabierane osobie, która przegrała walkę")
    @CustomKey("static-victim-change")
    public int staticVictimChange = 10;

    @Comment("")
    @Comment("Czy pokazywać informacje przy kliknięciu PPM na gracza")
    @CustomKey("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @Comment("")
    @Comment("Czy pokazać informacje z komendy /gracz przy kliknięciu PPM")
    @Comment("Jeśli wyłączone - pokazywane będą informacje z sekcji \"playerRightClickInfo\" z messages.yml")
    @CustomKey("info-player-command")
    public boolean infoPlayerCommand = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Cooldown pomiędzy pokazywaniem informacji przez PPM")
    @CustomKey("info-player-cooldown")
    public Duration infoPlayerCooldown = Duration.ofSeconds(5);

    @Comment("")
    @Comment("Czy trzeba kucać, aby przy kliknięciu PPM na gracza wyświetliło informacje o nim")
    @CustomKey("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @Comment("")
    @Comment("Czy członkowie gildii mogą sobie zadawać obrażenia (domyślnie)")
    @CustomKey("damage-guild")
    public boolean damageGuild = false;

    @Comment("")
    @Comment("Czy sojuszniczy mogą sobie zadawać obrażenia")
    @CustomKey("damage-ally")
    public boolean damageAlly = false;

    @PositiveOrZero
    @Comment("")
    @Comment("Co ile można użyć komendy /helprequest")
    public Duration helpRequestCooldown = Duration.ofSeconds(1);

    @Comment("")
    @Comment("Wygląd znacznika {POS} wstawionego w format chatu")
    @Comment("Znacznik ten pokazuje czy ktoś jest liderem, zastępcą lub zwykłym członkiem gildii")
    @AutoColor
    public String chatPosition = "&b{POS} ";

    @Comment("")
    @Comment("Wygląd znacznika {TAG} wstawionego w format chatu")
    @AutoColor
    public String chatGuild = "&b{TAG} ";

    @Comment("")
    @Comment("Wygląd znacznika {RANK} wstawionego w format chatu")
    @AutoColor
    public String chatRank = "&b{RANK} ";

    @Comment("")
    @Comment("Wygląd znacznika {POINTS} wstawionego w format chatu")
    @Comment("Możesz tu także użyć znacznika {POINTS-FORMAT}")
    @AutoColor
    public String chatPoints = "&b{POINTS} ";

    @Comment("")
    public TopConfiguration top = new TopConfiguration();

    @Comment("")
    @Comment("Wygląd znacznika {POINTS-FORMAT} i {G-AVG-POINTS-FORMAT} w zależności od wartości punktów")
    @Comment("{G-AVG-POINTS-FORMAT}, tak samo jak {G-AVG-POINTS}, jest używane jedynie na liście graczy")
    @Comment("Lista powinna być podana od najmniejszych do największych rankingów i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minRank-maxRank wygląd\", np.: \"0-750 &4{POINTS}\"")
    @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    public List<RangeFormatting> pointsFormat = Arrays.asList(
            new RangeFormatting(0, 749, "&4{POINTS}"),
            new RangeFormatting(750, 999, "&c{POINTS}"),
            new RangeFormatting(1000, 1499, "&a{POINTS}"),
            new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{POINTS}")
    );

    @Comment("")
    @Comment("Wygląd znacznika {MINUS-FORMATTED} i {PLUS-FORMATTED}, w zależności od wartości zmiany w rankingu")
    @Comment("Lista powinna być podana od najmniejszych do największych wartości i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minChange-maxChange wygląd\", np.: \"0-50 &a+{CHANGE}\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minChange w górę, np.: \"50-* &2+{CHANGE}\"")
    @Comment("  lub wszystkie wartości do danego maxChange w dół, np.: \"*-50 &4-{CHANGE}\"")
    public List<RangeFormatting> killPointsChangeFormat = Arrays.asList(
            new RangeFormatting(Integer.MIN_VALUE, -1, "&c-{CHANGE}"),
            new RangeFormatting(0, 0, "&7{CHANGE}"),
            new RangeFormatting(1, Integer.MAX_VALUE, "&a+{CHANGE}")
    );

    @Comment("")
    @Comment("Wygląd znacznika {PING-FORMAT} w zależności od wartości pingu")
    @Comment("Lista powinna być podana od najmniejszych do największych wartości i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minPing-maxPing wygląd\", np.: \"0-75 &a{PING}\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minPing w górę, np.: \"301-* &c{PING}\"")
    public List<RangeFormatting> pingFormat = Arrays.asList(
            new RangeFormatting(0, 75, "&a{PING}"),
            new RangeFormatting(76, 150, "&e{PING}"),
            new RangeFormatting(151, 300, "&c{PING}"),
            new RangeFormatting(301, Integer.MAX_VALUE, "&c{PING}")
    );

    @Comment("")
    @Comment("Czy tag gildii podany przy tworzeniu gildii powinien zachować formę taką, w jakiej został wpisany")
    @Comment("UWAGA: gdy ta opcja jest włączona, opcja \"guild-tag-uppercase\" nie będzie miała wpływu na tag gildii")
    @CustomKey("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @Comment("")
    @Comment("Czy tagi gildii powinny byc pokazywane wielkimi literami")
    @Comment("Działa dopiero po zrestartowaniu serwera")
    @CustomKey("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    public ItemNames itemNames = new ItemNames();

    public static class ItemNames extends ConfigSection {

        @Comment
        @Comment("Czy nazwy przedmiotów powinny być tłumaczone?")
        @Comment("Gdy ta opcja jest wyłączona zostaną użyte wewnętrzne nazwy przedmiotów, np. DIAMOND_SWORD")
        public boolean translateMaterialNames = true;

        @Comment
        @Comment("Czy do tłumaczenia nazw przedmiotów plugin ma używać tzw. TranslatableComponents - nazwy przedmiotów będą wyświetlane wtedy w języku gracza")
        public boolean useTranslatableComponents = false;

        @Comment
        @Comment("Wygląd znaczników {ITEM} i {ITEMS} za liczbą przedmiotu")
        @Comment("Dla np. item-amount-suffix: \"szt. \" otrzymamy 1szt. golden_apple")
        @AutoColor
        public String itemAmountSuffix = "x ";

    }

    @Comment("")
    @Comment("Czy sprawdzanie zakazanych nazw i tagów gildii powinno być włączone")
    @CustomKey("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @Comment("")
    @Comment("Jeśli ustawione na false - nazwy i tagi z list 'restricted-guild-names', 'restricted-guild-tags' będą niedozwolone")
    @Comment("Jeśli ustawione na true - jedynie nazwy i tagi z list 'restricted-guild-names', 'restricted-guild-tags' będą dozwolone")
    @Comment("Przydatne, gdy chcesz ograniczyć tworzenie np. do 2 gildii \"RED\", \"BLUE\"")
    @CustomKey("whitelist")
    public boolean whitelist = false;

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako nazwa gildii")
    @CustomKey("restricted-guild-names")
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako tag gildii")
    @CustomKey("restricted-guild-tags")
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @Comment("")
    @Comment("Czy powiadomienia o wejściu na teren gildii członka gildii powinny byc wyświetlane")
    @CustomKey("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    @Comment("")
    @Comment("Czy osoba, która założyła pierwszą gildię na serwerze powinna dostać nagrodę")
    @CustomKey("should-give-rewards-for-first-guild")
    public boolean giveRewardsForFirstGuild = false;

    @Comment("")
    @Comment("Przedmioty, które zostaną przyznane graczowi, który pierwszy założył gildię na serwerze")
    @CustomKey("rewards-for-first-guild")
    public List<ItemStack> firstGuildRewards = ItemUtils.parseItems("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze");

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu rankingu")
    @CustomKey("rank-reset-needed-items")
    public List<ItemStack> rankResetItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu statystyk")
    @CustomKey("stats-reset-needed-items")
    public List<ItemStack> statsResetItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Czy przy szukaniu danych o graczu ma byc pomijana wielkość znaków jego nicku")
    @CustomKey("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @Comment("")
    @Comment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyświetlanie powiadomień o wejściu na teren gildii)")
    @CustomKey("event-move")
    public boolean eventMove = true;

    @Min(1)
    @Comment("")
    @Comment("Co ile minut dane są automatycznie zapisywane")
    public int dataInterval = 1;

    @Comment("")
    @Comment("Jaki argument powinien zostać podany przez gracza, gdy chce zaprosić wszystkich graczy w danym promieniu")
    public String inviteCommandAllArgument = "*";

    @Comment("")
    @Comment("Czy wielkość liter powinna być ignorowana dla argumentu od zapraszania wszystkich graczy w danym promieniu")
    public boolean inviteCommandAllArgumentIgnoreCase = true;

    @Comment("")
    @Comment("Maksymalna odległość, w jakiej zapraszani są gracze w momencie użycia komendy \"/invite *\"")
    public double inviteCommandAllMaxRange = 50.0;

    @Comment("")
    @Comment("Domyślna odległość, w jakiej zapraszani są gracze w momencie użycia komendy \"/invite *\"")
    public double inviteCommandAllDefaultRange = 10.0;

    @Comment("")
    @Comment("Hooki do pluginów, które powinny zostać wyłączone, opcja ta powinna być stosowania jedynie w awaryjnych sytuacjach!")
    @Comment("Lista hooków, które można wyłączyć: WorldEdit, WorldGuard, Vault, PlaceholderAPI, DecentHolograms, HolographicDisplays, dynmap")
    @Comment("Aby zostawić wszystkie hooki włączone wystarczy wpisać: disabled-hooks: []")
    public Set<String> disabledHooks = Set.of("HolographicDisplays");

    @Comment("")
    @Comment("Konfiguracja hooku do pluginy 'dynmap'")
    public DynmapHook dynmapHook = new DynmapHook();

    public static class DynmapHook extends OkaeriConfig {

        @Comment("")
        @Comment("Co ile ticków etykiety znaczników mają być odświeżane (20 ticków = 1 sekunda)")
        public long updateInterval = 60 * 20;

        @Comment("")
        @Comment("Oznaczenie listy gildii na mapie")
        public String guildSetLabel = "Gildie";

        @Comment("")
        public Center center = new Center();

        public static class Center extends OkaeriConfig {

            @Comment("Czy wskaźnik środka gildii ma być widoczny na mapie")
            public boolean enabled = true;

            @Comment("Czy wskaźnik środka powinien używać współrzędnych Y najwyższego punktu zamiast rzeczywistej wysokości")
            @Comment("Zmiana ta pozwala ukryć rzeczywistą wysokość środka gildii, gdy używamy mapy 3D")
            public boolean hideCenterY = false;

            @Comment("")
            @Comment("Etykieta środka gildii")
            @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
            public String label = "{NAME} ({TAG})";

            @Comment("")
            @Comment("Ikona oznaczenia środka gildii")
            @Comment("Lista domyślnie wbudowanych w plugin 'dynmap': https://github.com/FunnyGuilds/FunnyGuilds/tree/4.x/assets/dynmap-markers.png-")
            public String icon = "shield";

        }

        @Comment("")
        public Area area = new Area();

        public static class Area extends OkaeriConfig {

            @Comment("Czy obszar gildii ma być widoczny na mapie")
            public boolean enabled = true;

            @Comment("")
            @Comment("Etykieta obszaru gildii")
            @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
            @Comment("Wspierane są tutaj znaczniki HTML")
            public String label = "<b>{NAME} ({TAG})</b><br><br>" +
                    "<b>Właściciel:</b> {OWNER}<br>" +
                    "<b>Gracze (ONL/ALL):</b> {MEMBERS-ONLINE}/{MEMBERS-ALL}<br><br>" +
                    "<b>Punkty (Średnio):</b> {AVG-POINTS}";

            @Comment("")
            @Comment("Konfiguracja wyglądu wypełnienia obszaru gildii")
            public Fill fill = new Fill();

            public static class Fill extends OkaeriConfig {

                @Comment("Przezroczystość wypełnienia:")
                @Comment(" > 0 - całkowicie przezroczyste")
                @Comment(" > 1 - całkowicie wypełnione")
                public double opacity = 0.6;

                @Comment("")
                @Comment("Kolor wypełnienia")
                public Color color = Color.fromRGB(25, 25, 25);

            }

            @Comment("")
            @Comment("Konfiguracja linii (granic) obszaru gildii")
            public Line line = new Line();

            public static class Line extends OkaeriConfig {

                @Comment("Przezroczystość linii:")
                @Comment(" > 0 - całkowicie przezroczyste")
                @Comment(" > 1 - całkowicie wypełnione")
                public double opacity = 1;

                @Comment("")
                @Comment("Grubość linii (w pikselach)")
                public int weight = 3;

                @Comment("")
                @Comment("Kolor linii")
                public Color color = Color.fromRGB(10, 10, 10);

            }

        }

    }

    @Comment("")
    @Comment("Typ zapisu danych:")
    @Comment(" FLAT - lokalne pliki")
    @Comment(" MYSQL - baza danych, kompatybilna z MySQL")
    @Comment(" MARIADB - baza danych, kompatybilna z MariaDB")
    public DataModel dataModel = DataModel.FLAT;

    @Comment("")
    @Comment("Dane wymagane do połączenia z bazą")
    @Comment("UWAGA: connectionTimeout jest w milisekundach!")
    @Comment(" ")
    @Comment("Sekcja poolSize odpowiada za liczbę zarezerwowanych połączeń, domyślna wartość 5 powinna wystarczyć")
    @Comment("Aby umożliwić FG automatyczne zarządzanie liczbą połączeń - ustaw poolSize na -1")
    @Comment(" ")
    @Comment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @Comment("Najlepiej zmieniać te nazwy tylko wtedy, gdy np. występuje konflikt z innym pluginem")
    @Comment("Aby zmienić nazwy tabel, gdy masz juz w bazie jakieś dane z FG:")
    @Comment("1. Wyłącz serwer")
    @Comment("2. Zmień dane w configu FG")
    @Comment("3. Zmień nazwy tabel w bazie używając np. phpMyAdmin")
    public MysqlConfiguration mysql = new MysqlConfiguration();

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<>();

        for (String guiEntry : contents) {
            ItemStack item = null;

            if (guiEntry.contains("GUI-")) {
                int index = LegacyUtils.getIndex(guiEntry);
                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            } else if (guiEntry.contains("VIPITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItemsVip.size()) {
                        item = this.createItemsVip.get(index - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItemsVip.size() + " or <= 0");
                }
            } else if (guiEntry.contains("ITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItems.size()) {
                        item = this.createItems.get(index - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItems.size() + " or <= 0");
                }
            } else {
                item = ItemUtils.parseItem(guiEntry);
            }

            if (item == null) {
                item = new ItemBuilder(Material.RED_STAINED_GLASS_PANE, 1)
                        .setName("&c&lERROR IN GUI CREATION: " + guiEntry, true).getItem();
            }

            items.add(item);
        }

        return items;
    }

    @Override
    public void processProperties() {
        if (this.availableLocales.add(this.defaultLocale)) {
            FunnyGuilds.getPluginLogger().parser("Default locale '" + this.defaultLocale + "' hasn't been added in available locales, adding it automatically");
        }

        if (this.fluidFlowOnlyOnRegions) {
            DynamicListenerStorage.fluidFlowEvent = true;
        }

        this.guiItems = this.loadGUI(this.guiItems_);

        if (!this.useCommonGUI) {
            this.guiItemsVip = this.loadGUI(this.guiItemsVip_);
        }

        if (this.rankSystem == RankSystem.Type.ELO) {
            this.eloConstants = new HashMap<>();

            NumberRange.parseIntegerRange(this.eloConstants_, false).forEach((key, value) -> {
                int constant = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(value)).orElseGet(() -> {
                    FunnyGuilds.getPluginLogger().parser("\"" + value + "\" is not a valid elo constant!");
                    return 0;
                });

                this.eloConstants.put(key, constant);
            });
        }

        this.explodeMaterials = new EnumMap<>(Material.class);
        for (Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            double chance = entry.getValue();
            if (chance < 0) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("*")) {
                this.allMaterialsAreExplosive = true;
                this.defaultExplodeChance = chance;
                continue;
            }

            Material material = MaterialUtils.parseMaterial(entry.getKey(), true);
            if (material == null || material == Material.AIR) {
                continue;
            }

            this.explodeMaterials.put(material, chance);
        }

        this.tntProtection.time.passingMidnight = this.tntProtection.time.startTime.getTime().isAfter(this.tntProtection.time.endTime.getTime());
    }

    public enum DataModel {

        FLAT(null),
        MYSQL("com.mysql.cj.jdbc.Driver"),
        MARIADB("org.mariadb.jdbc.Driver");

        private final @Nullable String jdbcClassName;

        DataModel(@Nullable String jdbcClassName) {
            this.jdbcClassName = jdbcClassName;
        }

        public boolean isSQL() {
            return this.jdbcClassName != null;
        }

        public @Nullable String getJDBCClassName() {
            return this.jdbcClassName;
        }

    }

}
