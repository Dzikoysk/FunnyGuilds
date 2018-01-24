package net.dzikoysk.funnyguilds.data.configs;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.RankSystem;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.element.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.util.elo.EloUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgExclude;
import org.diorite.cfg.annotations.CfgName;
import org.diorite.cfg.annotations.CfgStringStyle;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CfgClass(name = "PluginConfig")
@CfgDelegateDefault("{new}")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("                                #")
@CfgComment("          FunnyGuilds           #")
@CfgComment("         4.0.2 Tribute          #")
@CfgComment("                                #")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
public class PluginConfig {

    @CfgExclude
    public SimpleDateFormat dateFormat;
    
    @CfgComment("Wyswietlana nazwa pluginu")
    @CfgName("plugin-name")
    public String pluginName = "FunnyGuilds";
    
    @CfgComment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    @CfgName("update-info")
    public boolean updateInfo = true;

    @CfgComment("Mozliwosc zakladania gildii (mozna zmienic takze za pomoca komendy /ga enabled)")
    @CfgName("guilds-enabled")
    public boolean guildsEnabled = true;

    @CfgComment("Czy tworzenie regionow gildii (i inne zwiazane z nimi rzeczy) maja byc wlaczone")
    @CfgComment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @CfgComment("Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @CfgComment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @CfgComment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    @CfgName("regions-enabled")
    public boolean regionsEnabled = true;
    
    @CfgComment("Maksymalna dlugosc nazwy gildii")
    @CfgName("name-length")
    public int createNameLength = 22;

    @CfgComment("Minimalna dlugosc nazwy gildii")
    @CfgName("name-min-length")
    public int createNameMinLength = 4;

    @CfgComment("Maksymalna dlugosc tagu gildii")
    @CfgName("tag-length")
    public int createTagLength = 4;

    @CfgComment("Minimalna dlugosc tagu gildii")
    @CfgName("tag-min-length")
    public int createTagMinLength = 2;

    @CfgComment("Minimalna liczba graczy w gildii aby zaliczala sie do rankingu")
    @CfgName("guild-min-members")
    public int minMembersToInclude = 3;
    
    @CfgComment("Tekst wstawiany za pozycje w rankingu jesli gildia ma mniej niz minMembersToInclude czlonkow (lub gracz nie ma gildii)")
    @CfgName("guild-min-members-pos-string")
    public String minMembersPositionString = "Brak";

    @CfgComment("Przedmioty wymagane do zalozenia gildii")
    @CfgComment("Dwukropek i metadata po nazwie przedmiotu sa opcjonalne")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant]")
    @CfgComment("Spacja = _ ")
    @CfgComment("Nowa linia lore = #")
    @CfgComment("Aby w lore uzyc znaku # wstaw {HASH}")
    @CfgName("items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> items_ = Arrays.asList("5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!", "5 dirt", "5 tnt");

    @CfgExclude
    public List<ItemStack> createItems;

    @CfgComment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Dwukropek i metadata po nazwie przedmiotu sa opcjonalne")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant]")
    @CfgComment("Spacja = _ ")
    @CfgComment("Nowa linia lore = #")
    @CfgComment("Aby w lore uzyc znaku # wstaw {HASH}")
    @CfgName("items-vip")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> itemsVip_ = Collections.singletonList("1 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!");

    @CfgExclude
    public List<ItemStack> createItemsVip;

    @CfgComment("Czy opcja wymaganego rankingu do zalozenia gildi ma byc wlaczona?")
    @CfgName("rank-create-enable")
    public boolean rankCreateEnable = true;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi")
    @CfgName("rank-create")
    public int rankCreate = 1000;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi dla osoby z uprawnieniem funnyguilds.vip.rank")
    @CfgName("rank-create-vip")
    public int rankCreateVip = 800;

    @CfgComment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @CfgComment("Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    @CfgName("use-common-gui")
    public boolean useCommonGUI = false;
    
    @CfgComment("GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Jesli wlaczone jest use-common-gui - ponizsze GUI jest uzywane takze dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Kazda linijka listy oznacza jeden slot, liczba slotow powinna byc wielokrotnoscia liczby 9 i nie powinna byc wieksza niz 54")
    @CfgComment("Aby uzyc przedmiotu stworzonego w jednym slocie w innym mozna uzyc {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, ktory jest w pierwszym slocie")
    @CfgComment("Aby wstawic przedmiot na gildie nalezy uzyc {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildie")
    @CfgComment("Aby wstawic przedmiot na gildie z listy vip nalezy uzyc {VIPITEM-nr}")
    @CfgName("gui-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItems_ = Arrays.asList("1 stained_glass_pane name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
                    "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
                    "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");
    
    @CfgExclude
    public List<ItemStack> guiItems;
    
    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-title")
    public String guiItemsTitle_ = "&5&lPrzedmioty na gildie";
    
    @CfgExclude
    public String guiItemsTitle;
    
    @CfgComment("GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @CfgComment("Ponizsze GUI bedzie ignorowane jesli wlaczone jest use-common-gui")
    @CfgName("gui-items-vip")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItemsVip_ = Arrays.asList("1 stained_glass_pane name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
                    "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
                    "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @CfgExclude
    public List<ItemStack> guiItemsVip;
    
    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-vip-title")
    public String guiItemsVipTitle_ = "&5&lPrzedmioty na gildie (VIP)";
    
    @CfgExclude
    public String guiItemsVipTitle;
    
    @CfgComment("Czy do przedmiotow na gildie, ktore sa w GUI, maja byc dodawane dodatkowe linie opisu?")
    @CfgComment("Linie te mozna ustawic ponizej")
    @CfgName("add-lore-lines")
    public boolean addLoreLines = true;
    
    @CfgComment("Dodatkowe linie opisu, dodawane do kazdego przedmiotu, ktory jest jednoczesnie przedmiotem na gildie")
    @CfgComment("Dodawane linie nie zaleza od otwieranego GUI - sa wspolne dla zwyklego i VIP")
    @CfgComment("Mozliwe do uzycia zmienne:")
    @CfgComment("{PINV-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie")
    @CfgComment("{PINV-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie")
    @CfgComment("{EC-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma w enderchescie")
    @CfgComment("{EC-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma w enderchescie")
    @CfgComment("{ALL-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie i w enderchescie")
    @CfgComment("{ALL-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    @CfgName("gui-items-lore")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItemsLore_ = Arrays.asList("&aPosiadzasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
                    "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");
    
    @CfgExclude
    public List<String> guiItemsLore;
    
    @CfgComment("Odleglosc od spawnu")
    @CfgName("create-distance")
    public int createDistance = 100;

    @CfgComment("Blok, jaki pojawi sie pod nami po stworzeniu gildii")
    @CfgName("create-material")
    public String createStringMaterial = "ender crystal";

    @CfgExclude
    public Material createMaterial;

    @CfgComment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @CfgComment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CfgName("create-center-y")
    public int createCenterY = 60;

    @CfgComment("Czy ma sie tworzyc pusta przestrzen dookola centrum gildii")
    @CfgName("create-center-sphere")
    public boolean createCenterSphere = true;

    @CfgComment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    @CfgName("bugged-blocks")
    public boolean buggedBlocks = false;

    @CfgComment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete (warunek wymagany: bugged-blocks = true)")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgName("bugged-blocks-timer")
    public int buggedBlocksTimer = 20;

    @CfgComment("Maksymalna liczba czlonkow w gildii")
    @CfgName("max-members")
    public int inviteMembers = 15;

    @CfgComment("Swiaty, na ktorych zablokowane jest tworzenie gildii")
    @CfgName("blockedworlds")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    @CfgComment("Mozliwosc ucieczki z terenu innej gildii")
    @CfgComment("Funkcja niedostepna jesli mozliwosc teleportacji do gildii jest wylaczona")
    @CfgName("escape-enable")
    public boolean escapeEnable = true;
    
    @CfgComment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    @CfgName("escape-delay")
    public int escapeDelay = 120;
    
    @CfgComment("Mozliwosc teleportacji do gildii")
    @CfgName("base-enable")
    public boolean baseEnable = true;

    @CfgComment("Czas oczekiwania na teleportacje (w sekundach)")
    @CfgName("base-delay")
    public int baseDelay = 5;

    @CfgComment("Koszt teleportacji do gildii (jezeli brak, wystarczy zrobic: base-items: [])")
    @CfgName("base-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> baseItems_ = Arrays.asList("1 diamond", "1 emerald");

    @CfgExclude
    public List<ItemStack> baseItems;

    @CfgComment("Koszt dolaczenia do gildii (jezeli brak, wystarczy zrobic: join-items: [])")
    @CfgName("join-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> joinItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> joinItems;

    @CfgComment("Mozliwosc powiekszania gildii")
    @CfgName("enlarge-enable")
    public boolean enlargeEnable = true;

    @CfgComment("O ile powieksza teren gildii 1 poziom")
    @CfgName("enlarge-size")
    public int enlargeSize = 5;

    @CfgComment("Koszt powiekszania gildii")
    @CfgComment("- kazdy myslnik, to 1 poziom gildii")
    @CfgName("enlarge-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @CfgExclude
    public List<ItemStack> enlargeItems;

    @CfgComment("Wielkosc regionu gildii")
    @CfgName("region-size")
    public int regionSize = 50;

    @CfgComment("Minimalna odleglosc miedzy terenami gildii")
    @CfgName("region-min-distance")
    public int regionMinDistance = 10;

    @CfgComment("Czas wyswietlania powiadomienia na pasku powiadomien (w sekundach)")
    @CfgName("region-notification-time")
    public int regionNotificationTime = 15;

    @CfgComment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza (w sekundach)")
    @CfgName("region-notification-cooldown")
    public int regionNotificationCooldown = 60;

    @CfgComment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CfgName("region-commands")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionCommands = Collections.singletonList("sethome");

    @CfgComment("Czy mozna usunac gildie jezeli ktos spoza gildii jest na jej terenie")
    @CfgName("region-delete-if-near")
    public boolean regionDeleteIfNear = false;

    @CfgComment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    @CfgName("region-explode")
    public int regionExplode = 120;

    @CfgComment("Jaki ma byc zasieg pobieranych przedmiotow po wybuchu (jezeli chcesz wylaczyc, wpisz 0)")
    @CfgName("explode-radius")
    public int explodeRadius = 3;

    @CfgComment("Jakie materialy i z jaka szansa maja byc niszczone po wybuchu")
    @CfgComment("<material>: <szansa (w %)")
    @CfgName("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of("obsidian", 20.0, "water", 33.0, "lava", 33.0);

    @CfgExclude
    public Map<Material, Double> explodeMaterials;

    @CfgComment("Ile zyc ma gildia")
    @CfgName("war-lives")
    public int warLives = 3;

    @CfgComment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CfgName("war-protection")
    public String warProtection_ = "24h";

    @CfgExclude
    public long warProtection;

    @CfgComment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CfgName("war-wait")
    public String warWait_ = "24h";

    @CfgExclude
    public long warWait;

    @CfgComment("Jaka waznosc ma gildia po jej zalozeniu")
    @CfgName("validity-start")
    public String validityStart_ = "14d";

    @CfgExclude
    public long validityStart;

    @CfgComment("Ile czasu dodaje przedluzenie gildii")
    @CfgName("validity-time")
    public String validityTime_ = "14d";

    @CfgExclude
    public long validityTime;

    @CfgComment("Ile dni przed koncem wygasania mozna przedluzyc gildie (wpisz 0, jezeli funkcja ma byc wylaczona)")
    @CfgName("validity-when")
    public String validityWhen_ = "14d";

    @CfgExclude
    public long validityWhen;

    @CfgComment("Koszt przedluzenia gildii")
    @CfgName("validity-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> validityItems_ = Collections.singletonList("10 diamond");

    @CfgExclude
    public List<ItemStack> validityItems;

    @CfgComment("Ranking od ktorego rozpoczyna gracz")
    @CfgName("rank-start")
    public int rankStart = 1000;

    @CfgComment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    @CfgName("rank-farming-protect")
    public boolean rankFarmingProtect = true;

    @CfgComment("Czas (w sekundach) blokady nabijania rankingu po walce dwoch osob")
    @CfgName("rank-farming-cooldown")
    public int rankFarmingCooldown = 7200;

    @CfgComment("Czy system asyst ma byc wlaczony")
    @CfgName("rank-assist-enable")
    public boolean assistEnable = true;
    
    @CfgComment("Jaka czesc rankingu za zabicie idzie na konto zabojcy")
    @CfgComment("1 to caly ranking, 0 to nic")
    @CfgComment("Reszta rankingu rozdzielana jest miedzy osoby asystujace w zaleznosci od zadanych obrazen")
    @CfgName("rank-assist-killer-share")
    public double assistKillerShare = 0.5;
    
    @CfgComment("System rankingowy uzywany przez plugin, do wyboru:")
    @CfgComment("ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @CfgComment("PERCENT - system, ktory obu graczom zabiera procent rankingu osoby zabitej")
    @CfgComment("STATIC - system, ktory zawsze zabiera iles rankingu zabijajacemu i iles zabitemu")
    @CfgName("rank-system")
    public String rankSystem_ = "ELO";

    @CfgExclude
    public RankSystem rankSystem;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Lista stalych obliczen rankingowych ELO, uzywanych przy zmianach rankingu - im mniejsza stala, tym mniejsze zmiany rankingu")
    @CfgComment("Stale okreslaja tez o ile maksymalnie moze zmienic sie ranking pochodzacy z danego przedzialu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank stala\", np.: \"0-1999 32\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"2401-* 16\"")
    @CfgName("elo-constants")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> eloConstants = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-divider")
    public double eloDivider = 400.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-exponent")
    public double eloExponent = 10.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest PERCENT!")
    @CfgComment("Procent rankingu osoby zabitej o jaki zmienia sie rankingi po walce")
    @CfgName("percent-rank-change")
    public double percentRankChange = 1.0;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty dawane osobie, ktora wygrywa walke")
    @CfgName("static-attacker-change")
    public int staticAttackerChange = 15;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty zabierane osobie, ktora przegrywa walke")
    @CfgName("static-victim-change")
    public int staticVictimChange = 10;

    @CfgComment("Czy pokazywac informacje przy kliknieciu PPM na gracza")
    @CfgName("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @CfgComment("Cooldown pomiedzy pokazywaniem informacji przez PPM (w sekundach)")
    @CfgName("info-player-cooldown")
    public int infoPlayerCooldown = 0;

    @CfgComment("Czy trzeba kucac, zeby przy klikniecu PPM na gracza wyswietlilo informacje o nim")
    @CfgName("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @CfgComment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CfgName("damage-guild")
    public boolean damageGuild = false;

    @CfgComment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CfgName("damage-ally")
    public boolean damageAlly = false;

    @CfgComment("Wyglad znaczika {POS} wstawionego w format chatu")
    @CfgComment("Znacznik ten pokazuje czy ktos jest liderem, zastepca czy zwyklym czlonkiem gildii")
    @CfgName("chat-position")
    public String chatPosition_ = "&b{POS} ";
    
    @CfgExclude
    public String chatPosition;
    
    @CfgComment("Znacznik dla lidera gildii")
    @CfgName("chat-position-leader")
    public String chatPositionLeader = "**";
    
    @CfgComment("Znacznik dla zastepcy gildii")
    @CfgName("chat-position-deputy")
    public String chatPositionDeputy = "*";
    
    @CfgComment("Znacznik dla czlonka gildii")
    @CfgName("chat-position-member")
    public String chatPositionMember = "";
    
    @CfgComment("Wyglad znaczika {TAG} wstawionego w format chatu")
    @CfgName("chat-guild")
    public String chatGuild_ = "&b{TAG} ";

    @CfgExclude
    public String chatGuild;

    @CfgComment("Wyglad znaczika {RANK} wstawionego w format chatu")
    @CfgName("chat-rank")
    public String chatRank_ = "&b{RANK} ";

    @CfgExclude
    public String chatRank;

    @CfgComment("Wyglad znaczika {POINTS} wstawionego w format chatu")
    @CfgName("chat-points")
    public String chatPoints_ = "&b{POINTS} ";

    @CfgExclude
    public String chatPoints;

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do gildii gildii")
    @CfgName("chat-priv")
    public String chatPriv = "!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do sojusznikow gildii")
    @CfgName("chat-ally")
    public String chatAlly = "!!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do wszystkich gildii")
    @CfgName("chat-global")
    public String chatGlobal = "!!!";

    @CfgComment("Wyglad wiadomosci wysylanej na czacie gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-priv-design")
    public String chatPrivDesign_ = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatPrivDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-ally-design")
    public String chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatAllyDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-global-design")
    public String chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatGlobalDesign;

    @CfgComment("Wyglad tagu osob w gildii")
    @CfgName("prefix-our")
    public String prefixOur_ = "&a{TAG}&f ";

    @CfgExclude
    public String prefixOur;

    @CfgComment("Wyglad tagu gildii sojuszniczej")
    @CfgName("prefix-allies")
    public String prefixAllies_ = "&6{TAG}&f ";

    @CfgExclude
    public String prefixAllies;

    @CfgExclude
    public String prefixEnemies;

    @CfgComment("Wyglad tagu gildii neutralnej (widziany rowniez przez graczy bez gildii)")
    @CfgName("prefix-other")
    public String prefixOther_ = "&7{TAG}&f ";

    @CfgExclude
    public String prefixOther;

    @CfgComment("Czy wlaczyc dummy z punktami")
    @CfgName("dummy-enable")
    public boolean dummyEnable = true;

    @CfgComment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CfgName("dummy-suffix")
    public String dummySuffix_ = "pkt";

    @CfgExclude
    public String dummySuffix;

    @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_QUOTED)
    @CfgComment("Wyglad listy graczy, przedzial od 1 do 80")
    @CfgComment("> Spis zmiennych gracza:")
    @CfgComment("{PLAYER} - nazwa gracza")
    @CfgComment("{PING} - ping gracza")
    @CfgComment("{POINTS} - punkty gracza")
    @CfgComment("{POSITION} - pozycja gracza w rankingu")
    @CfgComment("{KILLS} - liczba zabojstw gracza")
    @CfgComment("{DEATHS} - liczba smierci gracza")
    @CfgComment("{KDR} - stosunek zabojstw do smierci gracza")
    @CfgComment("> Spis zmiennych gildyjnych:")
    @CfgComment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @CfgComment("{G-TAG} - tag gildii gracza")
    @CfgComment("{G-OWNER} - wlasciciel gildii")
    @CfgComment("{G-LIVES} - liczba zyc gildii")
    @CfgComment("{G-ALLIES} - liczba sojusznikow gildii")
    @CfgComment("{G-POINTS} - punkty gildii")
    @CfgComment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @CfgComment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @CfgComment("{G-DEATHS} - suma smierci czlonkow gildii")
    @CfgComment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @CfgComment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @CfgComment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @CfgComment("> Spis pozostalych zmiennych:")
    @CfgComment("{ONLINE} - liczba graczy online")
    @CfgComment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @CfgComment("{SECOND} - Sekunda")
    @CfgComment("{MINUTE} - Minuta")
    @CfgComment("{HOUR} - Godzina")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanym miejscu w rankingu (np. {PTOP-1}, {PTOP-60})")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CfgName("player-list")
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(1, "&7Nick: &b{PLAYER}")
            .put(2, "&7Ping: &b{PING}")
            .put(3, "&7Punkty: &b{POINTS}")
            .put(4, "&7Zabojstwa: &b{KILLS}")
            .put(5, "&7Smierci: &b{DEATHS}")
            .put(6, "&7KDR: &b{KDR}")
            .put(7, "&7Gildia: &b{G-NAME}")
            .put(9, "&7TAG: &b{G-TAG}")
            .put(10, "&7Punkty gildii: &b{G-POINTS}")
            .put(11, "&7Pozycja gildii: &b{G-POSITION}")
            .put(12, "&7Liczba graczy online: &b{G-MEMBERS-ONLINE}")
            .put(21, "&7Online: &b{ONLINE}")
            .put(22, "&7TPS: &b{TPS}")
            .put(41, "&bTop 3 Gildii")
            .put(42, "&71. &b{GTOP-1}")
            .put(43, "&72. &b{GTOP-2}")
            .put(44, "&73. &b{GTOP-3}")
            .put(61, "&bTop 3 Graczy")
            .put(62, "&71. &b{PTOP-1}")
            .put(63, "&72. &b{PTOP-2}")
            .put(64, "&73. &b{PTOP-3}")
            .build();

    @CfgComment("Wyglad naglowka w liscie graczy.")
    @CfgName("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.0.2 Tribute";

    @CfgComment("Wyglad stopki w liscie graczy.")
    @CfgName("player-list-footer")
    public String playerListFooter = "&7O, dziala! &8{HOUR}:{MINUTE}:{SECOND}";

    @CfgComment("Liczba pingu pokazana przy kazdej komorce.")
    @CfgName("player-list-ping")
    public int playerListPing = 0;

    @CfgComment("Tablista wlaczona")
    @CfgName("player-list-enable")
    public boolean playerlistEnable = false;

    @CfgComment("Wyglad punktow wyswietlanych przy gildii w rankingu")
    @CfgName("player-list-points")
    public String playerlistPoints_ = "&7[&b{POINTS}&7]";

    @CfgComment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @CfgComment("Dziala dopiero po zrestartowaniu serwera")
    @CfgName("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @CfgExclude
    public String playerlistPoints;

    @CfgComment("Czy wlaczyc tlumaczenie nazw przedmiotow przy zabojstwie")
    @CfgName("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @CfgComment("Tlumaczenia nazw przedmiotow przy zabojstwie")
    @CfgComment("Wypisywac w formacie nazwa_przedmiotu: \"tlumaczona nazwa przedmiotu\"")
    @CfgName("translated-materials-name")
    public Map<String, String> translatedMaterials_ = ImmutableMap.<String, String>builder()
            .put("diamond_sword", "&3diamentowy miecz")
            .put("iron_sword", "&7zelazny miecz")
            .build();

    @CfgExclude
    public Map<Material, String> translatedMaterials;

    @CfgComment("Czy filtry nazw i tagow gildii powinny byc wlaczone")
    @CfgName("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @CfgComment("Niedozwolone nazwy przy zakladaniu gildii")
    @CfgName("restricted-guild-names")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @CfgComment("Niedozwolone tagi przy zakladaniu gildii")
    @CfgName("restricted-guild-tags")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @CfgComment("Gdzie maja pojawiac sie wiadomosci zwiazane z poruszaniem sie po terenach gildii")
    @CfgComment("Mozliwe miejsca wyswietlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CfgName("region-move-notification-style")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionEnterNotificationStyle_ = Arrays.asList("ACTIONBAR", "BOSSBAR");
    
    @CfgExclude
    public List<NotificationStyle> regionEnterNotificationStyle = new ArrayList<NotificationStyle>();
    
    @CfgComment("Jak dlugo title/subtitle powinien sie pojawiac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-in")
    public int notificationTitleFadeIn = 10;

    @CfgComment("Jak dlugo title/subtitle powinien pozostac na ekranie gracza")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-stay")
    public int notificationTitleStay = 10;

    @CfgComment("Jak dlugo title/subtitle powinien znikac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-out")
    public int notificationTitleFadeOut = 10;

    @CfgComment("Zbior przedmiotow potrzebnych do resetu rankingu")
    @CfgName("rank-reset-needed-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> rankResetItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> rankResetItems;

    @CfgComment("Nazwy komend")
    @CfgName("commands")
    public Commands commands = new Commands();
    @CfgComment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CfgName("event-move")
    public boolean eventMove = true;
    @CfgExclude
    public boolean eventPhysics;
    @CfgComment("Co ile minut ma automatycznie zapisywac dane")
    @CfgName("data-interval")
    public int dataInterval = 1;
    @CfgComment("Typ zapisu danych")
    @CfgComment("Flat - Lokalne pliki")
    @CfgComment("MySQL - baza danych")
    @CfgName("data-type")
    public DataType dataType = new DataType(true, false);
    @CfgComment("Dane wymagane do polaczenia z baza")
    @CfgName("mysql")
    public MySQL mysql = new MySQL("localhost", 3306, "db", "root", "passwd", 16);

    private List<ItemStack> loadItemStackList(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        for (String item : strings) {
            if (item == null || "".equals(item)) {
                continue;
            }
            ItemStack itemstack = Parser.parseItem(item);
            if (itemstack != null) {
                items.add(itemstack);
            }
        }

        return items;
    }

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        
        for (int i=0; i < contents.size(); i++) {
            String var = contents.get(i);
            ItemStack item = null;
            
            if (var.contains("GUI-")) {
                item = items.get(Parser.getIndex(var) - 1);
            } else if (var.contains("VIPITEM-")) {
                try {
                    item = createItemsVip.get(Parser.getIndex(var) - 1);
                } catch(IndexOutOfBoundsException e) {
                    FunnyGuilds.parser("Index given in " + var + " is > " + createItemsVip.size() + " or <= 0");
                }
            }  else if (var.contains("ITEM-")) {
                try {
                    item = createItems.get(Parser.getIndex(var) - 1);
                } catch(IndexOutOfBoundsException e) {
                    FunnyGuilds.parser("Index given in " + var + " is > " + createItems.size() + " or <= 0");
                }
            } else {
                item = Parser.parseItem(var);
            }
            
            items.add(item);
        }
        
        return items;
    }
    
    public void reload() {
        this.dateFormat = new SimpleDateFormat(Messages.getInstance().dateFormat);
        
        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);
        
        this.guiItems = loadGUI(this.guiItems_);
        if (!useCommonGUI) {
            this.guiItemsVip = loadGUI(this.guiItemsVip_);
        }
        
        this.guiItemsTitle = StringUtils.colored(this.guiItemsTitle_);
        this.guiItemsVipTitle = StringUtils.colored(this.guiItemsVipTitle_);
        this.guiItemsLore = StringUtils.colored(this.guiItemsLore_);
        
        this.createMaterial = Parser.parseMaterial(this.createStringMaterial);

        if (this.createMaterial != null && this.createMaterial == Material.DRAGON_EGG) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        } else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        if (this.buggedBlocksTimer <= 0) {
            FunnyGuilds.exception(new IllegalArgumentException("The field named \"bugged-blocks-timer\" can not be less than or equal to zero!"));
            this.buggedBlocksTimer = 20; // default value
        }

        try {
            this.rankSystem = RankSystem.valueOf(this.rankSystem_.toUpperCase());
        } catch (Exception e) {
            this.rankSystem = RankSystem.ELO;
            FunnyGuilds.exception(new IllegalArgumentException("\"" + this.rankSystem_ + "\" is not a valid rank system!"));
        }

        if (this.rankSystem == RankSystem.ELO) {
            EloUtils.parseData(this.eloConstants);
        }

        HashMap<Material, Double> map = new HashMap<>();
        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            Material material = Parser.parseMaterial(entry.getKey());
            if (material == null || material == Material.AIR) {
                continue;
            }
            
            double chance = entry.getValue();
            if (chance == 0) {
                continue;
            }
            
            map.put(material, chance);
        }

        this.explodeMaterials = map;

        this.translatedMaterials = new HashMap<>();
        for (String materialName : translatedMaterials_.keySet()) {
            Material material = Material.matchMaterial(materialName.toUpperCase());
            if (material == null) {
                continue;
            }
            
            translatedMaterials.put(material, translatedMaterials_.get(materialName));
        }

        for (String s : this.regionEnterNotificationStyle_) {
            this.regionEnterNotificationStyle.add(NotificationStyle.valueOf(s.toUpperCase()));
        }

        if (this.notificationTitleFadeIn <= 0) {
            FunnyGuilds.exception(new IllegalArgumentException("The field named \"notification-title-fade-in\" can not be less than or equal to zero!"));
            this.notificationTitleFadeIn = 10;
        }

        if (this.notificationTitleStay <= 0) {
            FunnyGuilds.exception(new IllegalArgumentException("The field named \"notification-title-stay\" can not be less than or equal to zero!"));
            this.notificationTitleStay = 10;
        }

        if (this.notificationTitleFadeOut <= 0) {
            FunnyGuilds.exception(new IllegalArgumentException("The field named \"notification-title-fade-out\" can not be less than or equal to zero!"));
            this.notificationTitleFadeOut = 10;
        }

        this.rankResetItems = loadItemStackList(this.rankResetItems_);

        this.warProtection = Parser.parseTime(this.warProtection_);
        this.warWait = Parser.parseTime(this.warWait_);

        this.validityStart = Parser.parseTime(this.validityStart_);
        this.validityTime = Parser.parseTime(this.validityTime_);
        this.validityWhen = Parser.parseTime(this.validityWhen_);

        this.validityItems = this.loadItemStackList(this.validityItems_);

        this.joinItems = this.loadItemStackList(this.joinItems_);
        this.baseItems = this.loadItemStackList(this.baseItems_);

        this.prefixOur = StringUtils.colored(this.prefixOur_);
        this.prefixAllies = StringUtils.colored(this.prefixAllies_);
        this.prefixOther = StringUtils.colored(this.prefixOther_);

        this.dummySuffix = StringUtils.colored(this.dummySuffix_);

        this.chatPosition = StringUtils.colored(this.chatPosition_);
        this.chatGuild = StringUtils.colored(this.chatGuild_);
        this.chatRank = StringUtils.colored(this.chatRank_);
        this.chatPoints = StringUtils.colored(this.chatPoints_);

        this.chatPrivDesign = StringUtils.colored(this.chatPrivDesign_);
        this.chatAllyDesign = StringUtils.colored(this.chatAllyDesign_);
        this.chatGlobalDesign = StringUtils.colored(this.chatGlobalDesign_);

        this.playerlistPoints = StringUtils.colored(this.playerlistPoints_);
    }

    public static class Commands {
        public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

        public FunnyCommand guild = new FunnyCommand("gildia", Arrays.asList("gildie", "g"));
        public FunnyCommand create = new FunnyCommand("zaloz");
        public FunnyCommand delete = new FunnyCommand("usun");
        public FunnyCommand confirm = new FunnyCommand("potwierdz");
        public FunnyCommand invite = new FunnyCommand("zapros");
        public FunnyCommand join = new FunnyCommand("dolacz");
        public FunnyCommand leave = new FunnyCommand("opusc");
        public FunnyCommand kick = new FunnyCommand("wyrzuc");
        public FunnyCommand base = new FunnyCommand("baza");
        public FunnyCommand enlarge = new FunnyCommand("powieksz");
        public FunnyCommand ally = new FunnyCommand("sojusz");
        public FunnyCommand items = new FunnyCommand("przedmioty");
        public FunnyCommand escape = new FunnyCommand("ucieczka", Collections.singletonList("escape"));
        public FunnyCommand rankReset = new FunnyCommand("rankreset", Collections.singletonList("resetrank"));
        @CfgName("break")
        public FunnyCommand break_ = new FunnyCommand("rozwiaz");
        public FunnyCommand info = new FunnyCommand("info");
        public FunnyCommand player = new FunnyCommand("gracz");
        public FunnyCommand top = new FunnyCommand("top", Collections.singletonList("top10"));
        public FunnyCommand validity = new FunnyCommand("przedluz");
        public FunnyCommand leader = new FunnyCommand("lider", Collections.singletonList("zalozyciel"));
        public FunnyCommand deputy = new FunnyCommand("zastepca");
        public FunnyCommand ranking = new FunnyCommand("ranking");
        public FunnyCommand setbase = new FunnyCommand("ustawbaze", Collections.singletonList("ustawdom"));
        public FunnyCommand pvp = new FunnyCommand("pvp", Collections.singletonList("ustawpvp"));
        @CfgComment("Komendy administratora")
        public AdminCommands admin = new AdminCommands();

        public static class FunnyCommand {
            @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_SINGLE_QUOTED)
            public String name;
            @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
            public List<String> aliases;
            public boolean enabled;

            public FunnyCommand() {}

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

        public static class AdminCommands {
            public String main = "ga";
            public String add = "ga dodaj";
            public String delete = "ga usun";
            public String kick = "ga wyrzuc";
            public String teleport = "ga tp";
            public String points = "ga points";
            public String kills = "ga kills";
            public String deaths = "ga deaths";
            public String ban = "ga ban";
            public String lives = "ga zycia";
            public String move = "ga przenies";
            public String unban = "ga unban";
            public String validity = "ga przedluz";
            public String name = "ga nazwa";
            public String spy = "ga spy";
            public String enabled = "ga enabled";
            public String leader = "ga lider";
            public String deputy = "ga zastepca";
        }
    }

    public static class DataType {
        public boolean flat;
        public boolean mysql;

        public DataType() {}

        public DataType(boolean flat, boolean mysql) {
            this.flat = flat;
            this.mysql = mysql;
        }
    }

    public static class MySQL {
        public String hostname;
        public int port;
        public String database;
        public String user;
        public String password;
        public int poolSize;

        public MySQL() {
        }

        public MySQL(String hostname, int port, String database, String user, String password, int poolSize) {
            this.hostname = hostname;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
        }
    }
}
