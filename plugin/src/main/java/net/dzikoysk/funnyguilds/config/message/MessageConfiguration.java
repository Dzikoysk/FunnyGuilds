package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.MessageRepository;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.impl.BossBarHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.ChatHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.exception.OkaeriException;
import java.lang.reflect.Field;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.kyori.adventure.bossbar.BossBar;

@Header("Dla większości wiadomości poprawny jest format: https://github.com/P3ridot/YetAnotherMessagesLibrary/blob/master/repository/okaeri/FORMAT.md")
@Header("Pozwala on m.in. na zmianę miejsca wyświetlania wiadomości (np. wyświetlanie danej wiadomość na actionbarze zamiast chacie)")
@Header("Jeśli jednak wiadomość nie wspiera danego formatu to w konsoli pojawi się błąd")
@Header(" ")
@Header("Formatowanie samych wiadomości:")
@Header("  - MiniMessage -> https://docs.adventure.kyori.net/minimessage/format.html")
@Header("  - &X -> legacy formatowanie kolorów np. &c")
@Header("  - &#XXXXXX -> formatowanie kolorów HEX np. &#FF0000")
public class MessageConfiguration extends OkaeriConfig implements MessageRepository {

    @Comment("<------- Global Date Format -------> #")
    public FunnyTimeFormatter dateFormat = new FunnyTimeFormatter("dd.MM.yyyy HH:mm:ss");

    @Comment("")
    @Comment("<------- No Value Messages -------> #")
    public String gNameNoValue = "Brak (G-NAME/NAME)";
    public String gTagNoValue = "Brak (G-TAG/TAG)";
    public String gOwnerNoValue = "Brak (G-OWNER)";
    public String gDeputiesNoValue = "Brak (G-DEPUTIES)";
    public String gDeputyNoValue = "Brak (G-DEPUTY)";
    public String gValidityNoValue = "Brak (G-VALIDITY)";
    public String gProtectionNoValue = "Brak (G-PROTECTION)";
    public String gRegionSizeNoValue = "Brak (G-REGION-SIZE)";
    public String livesNoValue = "Brak (LIVES-SYMBOL/LIVES-SYMBOL-ALL)";
    public String alliesNoValue = "Brak (ALLIES)";
    public String enemiesNoValue = "Brak (ENEMIES)";
    public String gtopNoValue = "Brak (GTOP-x)";
    public String ptopNoValue = "Brak (PTOP-x)";
    public String wgRegionNoValue = "Brak (WG-REGION)";
    public String minMembersToIncludeNoValue = "Brak (guild-min-members w config.yml)";

    @Comment("")
    @Comment("<------- Permission Messages -------> #")
    public SendableMessage permission = ChatHolder.message("&cNie masz wystarczajacych uprawnien do uzycia tej komendy!");
    public SendableMessage blockedWorld = ChatHolder.message("&cZarzadzanie gildiami jest zablokowane na tym swiecie!");
    public SendableMessage playerOnly = ChatHolder.message("&cKomenda dostepna tylko dla graczy!");

    @Comment("")
    @Comment("<------- Rank Messages -------> #")
    public SendableMessage rankLastVictimV = ChatHolder.message("&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!");
    public SendableMessage rankLastVictimA = ChatHolder.message("&7Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!");
    public SendableMessage rankLastAttackerV = ChatHolder.message("&7Ostatnio zabiles tego samego gracza, punkty nie zostaja odebrane!");
    public SendableMessage rankLastAttackerA = ChatHolder.message("&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja dodane!");
    public SendableMessage rankIPVictim = ChatHolder.message("&7Ten gracz ma taki sam adres IP, punkty nie zostaja odjete!");
    public SendableMessage rankIPAttacker = ChatHolder.message("&7Ten gracz ma taki sam adres IP, punkty nie zostaja dodane!");
    public SendableMessage rankMemberVictim = ChatHolder.message("&7Ten gracz jest czlonkiem twojej gildii, punkty nie zostaja odebrane!");
    public SendableMessage rankMemberAttacker = ChatHolder.message("&7Ten gracz jest czlonkiem twojej gildii, punkty nie zostaja dodane!");
    public SendableMessage rankAllyVictim = ChatHolder.message("&7Ten gracz jest czlonkiem sojuszniczej gildii, punkty nie zostaja odebrane!");
    public SendableMessage rankAllyAttacker = ChatHolder.message("&7Ten gracz jest czlonkiem sojuszniczej gildii, punkty nie zostaja dodane!");
    @Comment("Dostępne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {MINUS-FORMATTED}, {PLUS-FORMATTED}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {WEAPON-NAME}, {ITEM}, {ITEM-NO-AMOUNT}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
    public SendableMessage rankDeathMessage = ChatHolder.message("{ATAG}&b{ATTACKER} &7({PLUS-FORMATTED}&7) zabil {VTAG}&b{VICTIM} &7({MINUS-FORMATTED}&7) uzywajac &b{ITEM-NO-AMOUNT}");
    public SendableMessage rankKillMessage = TitleHolder.message("&cZabiles gracza {VICTIM}", "&7{PLUS-FORMATTED}", 10, 10, 10);
    @Comment("Zamiast zmiennej {ASSISTS} wstawiane są kolejne wpisy o asystujących graczach")
    public String rankAssistMessage = "&7Asystowali: {ASSISTS}";
    @Comment("Dostępne zmienne: {PLAYER}, {+}, {PLUS-FORMATTED}, {SHARE}")
    public String rankAssistEntry = "&b{PLAYER} &7({PLUS-FORMATTED}&7, {SHARE}% dmg)";
    @Comment("Znaki oddzielające kolejne wpisy o asystujących graczach")
    public String rankAssistDelimiter = "&8, ";
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public SendableMessage rankResetItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
    @Comment("Dostępne zmienne: {LAST-RANK}, {CURRENT-RANK}")
    public SendableMessage rankResetMessage = ChatHolder.message("&7Zresetowales swoj ranking z poziomu &c{LAST-RANK} &7do poziomu &c{CURRENT-RANK}&7.");
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public SendableMessage statsResetItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
    @Comment("Dostępne zmienne: {LAST-POINTS}, {CURRENT-POINTS}, {LAST-KILLS}, {CURRENT-KILLS}, {LAST-DEATHS}, {CURRENT-DEATHS}, {LAST-ASSISTS}, {CURRENT-ASSISTS}, {LAST-LOGOUTS}, {CURRENT-LOGOUTS}")
    public SendableMessage statsResetMessage = ChatHolder.message(
            "&7Zresetowales swoje statystyki do podstawowych",
            " &7Punkty: &c{LAST-POINTS} &8-> &a{CURRENT-POINTS}",
            " &7Zabójstwa: &c{LAST-KILLS} &8-> &a{CURRENT-KILLS}",
            " &7Śmierci: &c{LAST-DEATHS} &8-> &a{CURRENT-DEATHS}",
            " &7Asysty: &c{LAST-ASSISTS} &8-> &a{CURRENT-ASSISTS}",
            " &7Wylogowania: &c{LAST-LOGOUTS} &8-> &a{CURRENT-LOGOUTS}"
    );

    @Comment("")
    @Comment("<------- Ban Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    public String banMessage = "&7Zostales zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}";

    @Comment("")
    @Comment("<------- Region Messages -------> #")
    public SendableMessage regionUnauthorized = ChatHolder.message("&cTen teren nalezy do innej gildii!");
    public SendableMessage regionCenter = ChatHolder.message("&cNie mozesz zniszczyc srodka swojej gildii!");
    public SendableMessage regionInteract = ChatHolder.message("&cNie mozesz ingerowac w okolice serca swojej gildii!");
    @Comment("Dostępne zmienne: {TIME}")
    public SendableMessage regionExplode = ChatHolder.message("&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!");
    @Comment("Dostępne zmienne: {TIME}")
    public SendableMessage regionExplodeInteract = ChatHolder.message("&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!");
    public SendableMessage regionOther = ChatHolder.message("&cNie mozesz tego zrobic na terenie gildii!");
    public SendableMessage regionCommand = ChatHolder.message("&cTej komendy nie mozna uzyc na terenie innej gildii!");
    public SendableMessage regionTeleport = ChatHolder.message("&cNie mozesz teleportowac sie na teren innej gildii!");
    public SendableMessage regionExplosionHasProtection = ChatHolder.message("&cEksplozja nie spowodowala zniszczen na terenie gildii, poniewaz jest ona chroniona!");
    public SendableMessage regionsDisabled = ChatHolder.message("&cRegiony gildii sa wylaczone!");

    @Comment("")
    @Comment("<------- Region Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage notificationIntruderEnterGuildRegion = SendableMessage.builder()
            .actionBar("&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!")
            .addHolders(BossBarHolder.builder("&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!")
                    .color(BossBar.Color.RED)
                    .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                    .stay(15 * 20)
                    .clearOtherBars(true)
                    .build())
            .build();
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage notificationEnterGuildRegion = SendableMessage.builder()
            .actionBar("&7Wkroczyles na teren gildii &c{TAG}&7!")
            .addHolders(BossBarHolder.builder("&7Wkroczyles na teren gildii &c{TAG}&7!")
                    .color(BossBar.Color.RED)
                    .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                    .stay(15 * 20)
                    .clearOtherBars(true)
                    .build())
            .build();
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage notificationLeaveGuildRegion = SendableMessage.builder()
            .actionBar("&7Opusciles teren gildii &c{TAG}&7!")
            .addHolders(BossBarHolder.builder("&7Opusciles teren gildii &c{TAG}&7!")
                    .color(BossBar.Color.GREEN)
                    .overlay(BossBar.Overlay.PROGRESS)
                    .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                    .stay(15 * 20)
                    .clearOtherBars(true)
                    .build())
            .build();

    @Comment("")
    @Comment("<------- Broadcast Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage broadcastCreate = ChatHolder.message("&a{PLAYER} &7zalozyl gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!");
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage broadcastDelete = ChatHolder.message("&c{PLAYER} &7rozwiazal gildie &c{TAG}&7!");
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage broadcastJoin = ChatHolder.message("&a{PLAYER} &7dolaczyl do gildii &a{TAG}&7!");
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage broadcastLeave = ChatHolder.message("&c{PLAYER} &7opuscil gildie &c{TAG}&7!");
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage broadcastKick = ChatHolder.message("&c{PLAYER} &7zostal &cwyrzucony &7z gildii &c{TAG}&7!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {REASON}, {TIME}")
    public SendableMessage broadcastBan = ChatHolder.message("&7Gildia &c{TAG}&7 zostala zbanowana za &c{REASON}&7, gratulacje!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage broadcastUnban = ChatHolder.message("&7Gildia &a{TAG}&7 zostala &aodbanowana&7!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    public SendableMessage broadcastValidity = ChatHolder.message("&7Gildia &b{TAG} &7wygasla&b! &7Jej baza znajdowala sie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!");
    @Comment("Dostępne zmienne: {WINNER}, {LOSER}")
    public SendableMessage broadcastWar = ChatHolder.message("&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!");
    public String noInformation = "Brak informacji";

    @Comment("")
    @Comment("<------- Help Messages -------> #")
    public SendableMessage funnyguildsHelp = ChatHolder.message(
            "&aFunnyGuilds Help:",
            "&b/funnyguilds (reload|rl) &7- przeladuj plugin",
            "&b/funnyguilds (update|check) &7- sprawdz dostepnosc aktualizacji",
            "&b/funnyguilds save-all &7- zapisz wszystko",
            "&b/funnyguilds funnybin &7- zapisz konfigurację online (~ usprawnia pomoc na &ahttps://github.com/FunnyGuilds/FunnyGuilds/issues>&7)"
    );

    public SendableMessage helpList = ChatHolder.message(
            "&7---------------------&8[ &aGildie &8]&7---------------------",
            "&a/zaloz [tag] [nazwa] &8- &7Tworzy gildie",
            "&a/zapros [gracz] &8- &7Zaprasza gracza do gildii",
            "&a/dolacz [tag] &8- &7Przyjmuje zaproszenie do gildii",
            "&a/info [tag] &8- &7Informacje o danej gildii",
            "&a/baza &8- &7Teleportuje do bazy gildii",
            "&a/powieksz &8- &7Powieksza teren gildii",
            "&a/przedluz &8- &7Przedluza waznosc gildii",
            "&a/lider [gracz] &8- &7Oddaje zalozyciela gildii",
            "&a/zastepca [gracz] &8- &7Nadaje zastepce gildii",
            "&a/sojusz [tag] &8- &7Pozwala nawiazac sojusz",
            "&a/opusc &8- &7Opuszcza gildie",
            "&a/wyrzuc [gracz] &8- &7Wyrzuca gracza z gildii",
            "&a/rozwiaz [tag] &8- &7Rozwiazuje sojusz",
            "&a/usun &8- &7Usuwa gildie",
            "&a/przedmioty &8- &7Pokazuje przedmioty potrzebne do zalozenia gildii",
            "&a/ucieczka &8- &7Rozpoczyna ucieczke z terenu innej gildii"
    );

    public SendableMessage adminHelpList = ChatHolder.message(
            "&a/ga dodaj [tag] [nick] &8- &7Dodaje gracza do gildii",
            "&a/ga usun [tag] &8- &7Usuwa gildie",
            "&a/ga wyrzuc [nick] &8- &7Wyrzuca gracza z gildii",
            "&a/ga tp [tag] &8- &7Teleportuje do bazy gildii",
            "&a/ga points [nick] [points] &8- &7Ustawia liczbe punktow gracza",
            "&a/ga kills [nick] [kills] &8- &7Ustawia liczbe zabojstw gracza",
            "&a/ga deaths [nick] [deaths] &8- &7Ustawia liczbe smierci gracza",
            "&a/ga ban [tag] [czas] [powod] &8- &7Banuje gildie na okreslony czas",
            "&a/ga unban [tag] &8- &7Odbanowywuje gildie",
            "&a/ga zycia [tag] [zycia] &8- &7Ustawia liczbe zyc gildii",
            "&a/ga przenies [tag] &8- &7Przenosi teren gildii",
            "&a/ga przedluz [tag] [czas] &8- &7Przedluza waznosc gildii o podany czas",
            "&a/ga ochrona [tag] [czas] &8- &7Ustawia date wygasniecia ochrony",
            "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwe gildii",
            "&a/ga tag [tag] [nowy tag] &8- &7Zmienia tag gildii",
            "&a/ga spy &8- &7Szpieguje czat gildii",
            "&a/ga enabled &8- &7Zarzadzanie statusem zakladania gildii",
            "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
            "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastepce gildii",
            "&a/ga baza [gracz] &8- &7Teleportuje gracza do bazy jego gildii"
    );

    @Comment("")
    @Comment("<------- Info Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {RANK}")
    public SendableMessage playerInfoList = ChatHolder.message(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&7Zabojstwa: &a{KILLS}",
            "&7Smierci: &a{DEATHS}",
            "&7Asysty: &a{ASSISTS}",
            "&7Logouty: &a{LOGOUTS}",
            "&7KDR: &a{KDR}",
            "&8-------------.------------------"
    );

    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {RANK}")
    public SendableMessage playerRightClickInfo = ChatHolder.message(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&8-------------.------------------"
    );

    public SendableMessage infoTag = ChatHolder.message("&cPodaj tag gildii!");
    public SendableMessage infoExists = ChatHolder.message("&cGildia o takim tagu nie istnieje!");

    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {AVG-POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
    public SendableMessage infoList = ChatHolder.message(
            "&8-------------------------------",
            "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
            "&7Zalozyciel: &c{OWNER}",
            "&7Zastepcy: &c{DEPUTIES}",
            "&7Punkty: &c{POINTS} &8[&c{POSITION-AVG_POINTS}&8]",
            "&7Ochrona: &c{PROTECTION}",
            "&7Zycia: &4{LIVES}",
            "&7Waznosc: &c{VALIDITY}",
            "&7Czlonkowie: &7{MEMBERS}",
            "&7Sojusze: &c{ALLIES}",
            "&7Wojny: &c{ENEMIES}",
            "&8-------------------------------"
    );

    @Comment("")
    @Comment("<------- Top Messages -------> #")
    @Comment("{GTOP-<typ>-<pozycja>} - gildia na podanej pozycji w topce dla danego typu. Lista dostępnych typów znajduje się w 'config.yml' pod kluczem 'top.enabled-guild-tops'")
    public SendableMessage topList = ChatHolder.message(
            "&8----------{ &cTOP 10 &8}----------",
            "&71&8. &c{GTOP-AVG_POINTS-1}",
            "&72&8. &c{GTOP-AVG_POINTS-2}",
            "&73&8. &c{GTOP-AVG_POINTS-3}",
            "&74&8. &c{GTOP-AVG_POINTS-4}",
            "&75&8. &c{GTOP-AVG_POINTS-5}",
            "&76&8. &c{GTOP-AVG_POINTS-6}",
            "&77&8. &c{GTOP-AVG_POINTS-7}",
            "&78&8. &c{GTOP-AVG_POINTS-8}",
            "&79&8. &c{GTOP-AVG_POINTS-9}",
            "&710&8. &c{GTOP-AVG_POINTS-10}"
    );

    @Comment("")
    @Comment("<------- Ranking Messages -------> #")
    @Comment("{PTOP-<typ>-<pozycja>} - gracz na podanej pozycji w topce dla danego typu. Lista dostępnych typów znajduje się w 'config.yml' pod kluczem 'top.enabled-user-tops'")
    public SendableMessage rankingList = ChatHolder.message(
            "&8----------{ &cTOP 10 Graczy &8}----------",
            "&71&8. &c{PTOP-POINTS-1}",
            "&72&8. &c{PTOP-POINTS-2}",
            "&73&8. &c{PTOP-POINTS-3}",
            "&74&8. &c{PTOP-POINTS-4}",
            "&75&8. &c{PTOP-POINTS-5}",
            "&76&8. &c{PTOP-POINTS-6}",
            "&77&8. &c{PTOP-POINTS-7}",
            "&78&8. &c{PTOP-POINTS-8}",
            "&79&8. &c{PTOP-POINTS-9}",
            "&710&8. &c{PTOP-POINTS-10}"
    );

    @Comment("")
    @Comment("<------- General Messages -------> #")
    public SendableMessage generalHasGuild = ChatHolder.message("&cMasz juz gildie!");
    public SendableMessage generalNoNameGiven = ChatHolder.message("&cPodaj nazwe gildii!");
    public SendableMessage generalHasNoGuild = ChatHolder.message("&cNie masz gildii!");
    public SendableMessage generalIsNotOwner = ChatHolder.message("&cNie jestes zalozycielem gildii!");
    public SendableMessage generalNoTagGiven = ChatHolder.message("&cPodaj tag gildii!");
    public SendableMessage generalNoNickGiven = ChatHolder.message("&cPodaj nick gracza!");
    public SendableMessage generalUserHasGuild = ChatHolder.message("&cTen gracz ma juz gildie!");
    public SendableMessage generalNoGuildFound = ChatHolder.message("&cTaka gildia nie istnieje!");
    public SendableMessage generalNotPlayedBefore = ChatHolder.message("&cTen gracz nigdy nie byl na serwerze!");
    public SendableMessage generalNotOnline = ChatHolder.message("&cTen gracz nie jest obecnie na serwerze!");

    @Comment("Dostępne zmienne: {TAG}")
    public SendableMessage generalGuildNotExists = ChatHolder.message("&7Gildia o tagu &c{TAG} &7nie istnieje!");
    public SendableMessage generalIsNotMember = ChatHolder.message("&cTen gracz nie jest czlonkiem twojej gildii!");
    public SendableMessage generalPlayerHasNoGuild = ChatHolder.message("&cTen gracz nie ma gildii!");
    public SendableMessage generalCommandDisabled = ChatHolder.message("&cTa komenda jest wylaczona!");
    public SendableMessage generalAllyPvpDisabled = ChatHolder.message("&cPVP pomiedzy sojuszami jest wylaczone w konfiguracji!");

    @Comment("")
    @Comment("<------- Escape Messages -------> #")
    public SendableMessage escapeDisabled = ChatHolder.message("&cPrzykro mi, ucieczki sa wylaczone!");
    @Comment("Dostępne zmienne: {TIME}")
    public SendableMessage escapeStartedUser = ChatHolder.message("&aDobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!");
    @Comment("Dostępne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    public SendableMessage escapeStartedOpponents = ChatHolder.message("&cGracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})");
    public SendableMessage escapeCancelled = ChatHolder.message("&cUcieczka zostala przerwana!");
    public SendableMessage escapeInProgress = ChatHolder.message("&cUcieczka juz trwa!");
    public SendableMessage escapeSuccessfulUser = ChatHolder.message("&aUdalo ci sie uciec!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage escapeSuccessfulOpponents = ChatHolder.message("&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!");
    public SendableMessage escapeNoUserGuild = ChatHolder.message("&cNie masz gildii do ktorej moglbys uciekac!");
    public SendableMessage escapeNoNeedToRun = ChatHolder.message("&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?");
    public SendableMessage escapeOnYourRegion = ChatHolder.message("&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?");

    @Comment("")
    @Comment("<------- Create Guild Messages -------> #")
    @Comment("Dostępne zmienne: {LENGTH}")
    public SendableMessage createTagLength = ChatHolder.message("&7Tag nie moze byc dluzszy niz &c{LENGTH} litery&7!");
    @Comment("Dostępne zmienne: {LENGTH}")
    public SendableMessage createNameLength = ChatHolder.message("&cNazwa nie moze byc dluzsza niz &c{LENGTH} litery&7!");
    @Comment("Dostępne zmienne: {LENGTH}")
    public SendableMessage createTagMinLength = ChatHolder.message("&7Tag nie moze byc krotszy niz &c{LENGTH} litery&7!");
    @Comment("Dostępne zmienne: {LENGTH}")
    public SendableMessage createNameMinLength = ChatHolder.message("&cNazwa nie moze byc krotsza niz &c{LENGTH} litery&7!");
    public SendableMessage createOLTag = ChatHolder.message("&cTag gildii moze zawierac tylko litery!");
    public SendableMessage createOLName = ChatHolder.message("&cNazwa gildii moze zawierac tylko litery!");
    public SendableMessage createMore = ChatHolder.message("&cNazwa gildi nie moze zawierac spacji!");
    public SendableMessage createNameExists = ChatHolder.message("&cJest juz gildia z taka nazwa!");
    public SendableMessage createTagExists = ChatHolder.message("&cJest juz gildia z takim tagiem!");
    public SendableMessage restrictedGuildName = ChatHolder.message("&cPodana nazwa gildii jest niedozwolona.");
    public SendableMessage restrictedGuildTag = ChatHolder.message("&cPodany tag gildii jest niedozwolony.");
    public SendableMessage invalidGuildLocation = ChatHolder.message("&cNie mozesz stworzyc gildii w tym miejscu!");
    @Comment("Dostępne zmienne: {DISTANCE}")
    public SendableMessage createSpawn = ChatHolder.message("&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}");
    public SendableMessage createIsNear = ChatHolder.message("&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!");
    @Comment("Dostępne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
    public SendableMessage createRank = ChatHolder.message("&cAby zalozyc gildie, wymagane jest przynajmniej &7{REQUIRED} &cpunktow.");
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public SendableMessage createItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}&c. Najedz na przedmiot, aby dowiedziec sie wiecej");
    @Comment("Dostępne zmienne: {EXP}")
    public SendableMessage createExperience = ChatHolder.message("&cNie posiadasz wymaganego doswiadczenia do zalozenia gildii: &7{EXP}");
    @Comment("Dostępne zmienne: {MONEY}")
    public SendableMessage createMoney = ChatHolder.message("&cNie posiadasz wymaganej ilosci pieniedzy do zalozenia gildii: &7{MONEY}");
    public SendableMessage withdrawError = ChatHolder.message("&cNie udalo sie pobrac pieniedzy z twojego konta z powodu: &7{ERROR}");
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public SendableMessage createGuild = ChatHolder.message("&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!");
    public SendableMessage createGuildCouldNotPasteSchematic = ChatHolder.message("&cWystapil blad podczas tworzenia terenu gildii, zglos sie do administracji.");
    @Comment("Dostępne zmienne: {BORDER-MIN-DISTANCE}")
    public SendableMessage createNotEnoughDistanceFromBorder = ChatHolder.message("&cJestes zbyt blisko granicy mapy aby zalozyc gildie! (Minimalna odleglosc: {BORDER-MIN-DISTANCE})");

    @Comment("")
    @Comment("<------- Delete Guild Messages -------> #")
    public SendableMessage deleteConfirm = ChatHolder.message("&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz");
    public SendableMessage deleteToConfirm = ChatHolder.message("&cNie masz zadnych dzialan do potwierdzenia!");
    public SendableMessage deleteSomeoneIsNear = ChatHolder.message("&cNie mozesz usunac gildii, ktos jest w poblizu!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage deleteSuccessful = ChatHolder.message("&7Pomyslnie &cusunieto &7gildie!");

    @Comment("")
    @Comment("<------- Invite Messages -------> #")
    @Comment("Dostępne zmienne: {AMOUNT}")
    public SendableMessage inviteAmount = ChatHolder.message("&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT}&7)");
    public SendableMessage inviteAmountJoin = ChatHolder.message("&7Ta gildia osiagnela juz &cmaksymalna &7liczbe czlonkow! (&c{AMOUNT}&7)");
    public SendableMessage inviteAllyAmount = ChatHolder.message("&7Osiagnieto juz &cmaksymalna &7liczbe sojuszy miedzygildyjnych! (&c{AMOUNT}&7)");
    @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public SendableMessage inviteAllyTargetAmount = ChatHolder.message("&7Gildia {TAG} posiada juz maksymalna liczbe sojuszy! (&c{AMOUNT}&7)");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage inviteCancelled = ChatHolder.message("&cCofnieto zaproszenie dla {PLAYER}!");
    @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
    public SendableMessage inviteCancelledToInvited = ChatHolder.message("&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage inviteToOwner = ChatHolder.message("&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!");
    @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
    public SendableMessage inviteToInvited = ChatHolder.message("&aOtrzymano zaproszenie do gildii &7{TAG}&a!");

    public SendableMessage inviteNoOneIsNearby = ChatHolder.message("&cNikogo nie ma w pobliżu!");

    @Comment("Dostępne zmienne: {RANGE}")
    public SendableMessage inviteAllCommand = ChatHolder.message("&aZapraszam wszystkich w promieniu {RANGE} bloków!");

    @Comment("Dostępne zmienne: {MAX_RANGE}")
    public SendableMessage inviteRangeToBig = ChatHolder.message("&cPodany zasięg jest zbyt duży! (Maksymalnie: {MAX_RANGE})");

    @Comment("Dostępne zmienne: {MAX_RANGE}")
    public SendableMessage inviteAllArgumentIsNotNumber = ChatHolder.message("&cPodany zasięg nie jest poprawną liczbą!");

    @Comment("")
    @Comment("<------- Join Messages -------> #")
    public SendableMessage joinHasNotInvitation = ChatHolder.message("&cNie masz zaproszenia do gildii!");
    public SendableMessage joinHasNotInvitationTo = ChatHolder.message("&cNie otrzymales zaproszenia do tej gildii!");
    public SendableMessage joinHasGuild = ChatHolder.message("&cMasz juz gildie!");
    public SendableMessage joinTagExists = ChatHolder.message("&cNie ma gildii o takim tagu!");
    @Comment("Dostępne zmienne: {GUILDS}")
    public SendableMessage joinInvitationList = ChatHolder.message(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Wpisz &a/dolacz [tag] &7aby dolaczyc do wybranej gildii"
    );

    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public SendableMessage joinItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage joinToMember = ChatHolder.message("&aDolaczyles do gildii &7{GUILD}");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage joinToOwner = ChatHolder.message("&a{PLAYER} &7dolaczyl do &aTwojej &7gildii!");

    @Comment("")
    @Comment("<------- Leave Messages -------> #")
    public SendableMessage leaveIsOwner = ChatHolder.message("&cZalozyciel &7nie moze opuscic gildii!");
    @Comment("Dostępne zmienne: {GUILDS}, {TAG}")
    public SendableMessage leaveToUser = ChatHolder.message("&7Opusciles gildie &a{GUILD}&7!");

    @Comment("")
    @Comment("<------- Kick Messages -------> #")
    public SendableMessage kickOtherGuild = ChatHolder.message("&cTen gracz nie jest w Twojej gildii!");
    public SendableMessage kickOwner = ChatHolder.message("&cNie mozna wyrzucic zalozyciela!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {PLAYER}")
    public SendableMessage kickToOwner = ChatHolder.message("&c{PLAYER} &7zostal wyrzucony z gildii!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage kickToPlayer = ChatHolder.message("&cZostales wyrzucony z gildii!");

    @Comment("")
    @Comment("<------- Enlarge Messages -------> #")
    public SendableMessage enlargeMaxSize = ChatHolder.message("&cOsiagnieto juz maksymalny rozmiar terenu!");
    public SendableMessage enlargeIsNear = ChatHolder.message("&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!");
    @Comment("Dostępne zmienne: {ITEM}")
    public SendableMessage enlargeItem = ChatHolder.message("&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}");
    @Comment("Dostępne zmienne: {SIZE}, {LEVEL}")
    public SendableMessage enlargeDone = ChatHolder.message("&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)");

    @Comment("")
    @Comment("<------- Base Messages -------> #")
    public SendableMessage baseTeleportationDisabled = ChatHolder.message("&cTeleportacja do baz gildyjnych nie jest dostepna");
    public SendableMessage baseHasNotRegion = ChatHolder.message("&cTwoja gildia nie posiada terenu!");
    public SendableMessage baseHasNotCenter = ChatHolder.message("&cTwoja gildia nie posiada srodka regionu!");
    public SendableMessage baseIsTeleportation = ChatHolder.message("&cWlasnie sie teleportujesz!");
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public SendableMessage baseItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
    public SendableMessage baseDontMove = ChatHolder.message("&7Nie ruszaj sie przez &c{TIME} &7sekund!");
    public SendableMessage baseMove = ChatHolder.message("&cRuszyles sie, teleportacja przerwana!");
    public SendableMessage baseTeleport = ChatHolder.message("&aTeleportacja&7...");

    @Comment("")
    @Comment("<------- Enemy Messages -------> #")
    public SendableMessage enemyCorrectUse = ChatHolder.message("&7Aby rozpoczac wojne z gildia wpisz &c/wojna [tag]");
    public SendableMessage enemySame = ChatHolder.message("&cNie mozesz rozpoczac wojny z wlasna gildia!");
    public SendableMessage enemyAlly = ChatHolder.message("&cNie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!");
    public SendableMessage enemyAlready = ChatHolder.message("&cProwadzisz juz wojne z ta gildia!");
    @Comment("Dostępne zmienne: {AMOUNT}")
    public SendableMessage enemyMaxAmount = ChatHolder.message("&7Osiagnieto juz &cmaksymalna &7liczbe wojen miedzygildyjnych! (&c{AMOUNT}&7)");
    @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public SendableMessage enemyMaxTargetAmount = ChatHolder.message("&7Gildia {TAG} posiada juz maksymalna liczbe wojen! (&c{AMOUNT}&7)");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage enemyDone = ChatHolder.message("&7Wypowiedziano gildii &a{GUILD}&7 wojne!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage enemyIDone = ChatHolder.message("&7Gildia &a{GUILD} &7wypowiedziala twojej gildii wojne!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage enemyEnd = ChatHolder.message("&7Zakonczono wojne z gildia &a{GUILD}&7!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage enemyIEnd = ChatHolder.message("&7Gildia &a{GUILD} &7zakonczyla wojne z twoja gildia!");

    @Comment("")
    @Comment("<------- Ally Messages -------> #")
    public SendableMessage allyHasNotInvitation = ChatHolder.message("&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]");
    @Comment("Dostępne zmienne: {GUILDS}")
    public SendableMessage allyInvitationList = ChatHolder.message(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Aby zaakceptowac uzyj &a/sojusz [tag]"
    );

    @Comment("Dostępne zmienne: {TAG}")
    public SendableMessage allyAlly = ChatHolder.message("&cMasz juz sojusz z ta gildia!");
    public SendableMessage allyDoesntExist = ChatHolder.message("&cNie posiadasz sojuszu z ta gildia!");
    public SendableMessage allySame = ChatHolder.message("&cNie mozesz nawiazac sojuszu z wlasna gildia!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyDone = ChatHolder.message("&7Nawiazano sojusz z gildia &a{GUILD}&7!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyIDone = ChatHolder.message("&7Gildia &a{GUILD} &7przystapila do sojuszu!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyReturn = ChatHolder.message("&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyIReturn = ChatHolder.message("&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyInviteDone = ChatHolder.message("&7Zaproszono gildie &a{GUILD} &7do sojuszu!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage allyToInvited = ChatHolder.message("&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!");

    @Comment("")
    @Comment("<------- Break Messages -------> #")
    public SendableMessage breakHasNotAllies = ChatHolder.message("&cTwoja gildia nie posiada sojuszy!");
    @Comment("Dostępne zmienne: {GUILDS}")
    public SendableMessage breakAlliesList = ChatHolder.message(
            "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
            "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]"
    );

    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage breakAllyExists = ChatHolder.message("&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7, &c{GUILD}&7)!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage breakDone = ChatHolder.message("&7Rozwiazano sojusz z gildia &c{GUILD}&7!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage breakIDone = ChatHolder.message("&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!");

    @Comment("")
    @Comment("<------- Validity Messages -------> #")
    @Comment("Dostępne zmienne: {TIME}")
    public SendableMessage validityWhen = ChatHolder.message("&7Gildie mozesz przedluzyc dopiero za &c{TIME}&7!");
    @Comment("Dostępne zmienne: {ITEM}")
    public SendableMessage validityItems = ChatHolder.message("&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}");
    @Comment("Dostępne zmienne: {DATE}")
    public SendableMessage validityDone = ChatHolder.message("&7Waznosc gildii przedluzona do &a{DATE}&7!");

    @Comment("")
    @Comment("<------- War Messages -------> #")
    public SendableMessage warDisabled = ChatHolder.message("&cPodbijanie gildii jest wyłączone.");
    public SendableMessage warHasNotGuild = ChatHolder.message("&cMusisz miec gildie, aby zaatkowac inna!");
    public SendableMessage warAlly = ChatHolder.message("&cNie mozesz zaatakowac sojusznika!");
    @Comment("Dostępne zmienne: {TIME}")
    public SendableMessage warWait = ChatHolder.message("&7Atak na gildie mozliwy za &4{TIME}");
    @Comment("Dostępne zmienne: {ATTACKED}")
    public SendableMessage warAttacker = ChatHolder.message("&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!");
    @Comment("Dostępne zmienne: {ATTACKER}")
    public SendableMessage warAttacked = ChatHolder.message("&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!");
    @Comment("Dostępne zmienne: {LOSER}")
    public SendableMessage warWin = ChatHolder.message("&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!");
    @Comment("Dostępne zmienne: {WINNER}")
    public SendableMessage warLose = ChatHolder.message("&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!");

    @Comment("")
    @Comment("<------- Leader Messages -------> #")
    public SendableMessage leaderMustBeDifferent = ChatHolder.message("&cNie mozesz sobie oddac zalozyciela!");
    public SendableMessage leaderSet = ChatHolder.message("&7Ustanowiono nowego &alidera &7gildii!");
    public SendableMessage leaderOwner = ChatHolder.message("&7Zostales nowym &aliderem &7gildii!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage leaderMembers = ChatHolder.message("&7{PLAYER} zostal nowym &aliderem &7gildii!");

    @Comment("")
    @Comment("<------- TNT Hours Messages -------> #")
    public SendableMessage tntInfo = ChatHolder.message("&7TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}");
    public SendableMessage tntProtectDisable = ChatHolder.message("&7TNT wybucha o każdej porze.");
    public SendableMessage tntNowEnabled = ChatHolder.message("&aTNT aktualnie jest włączone.");
    public SendableMessage tntNowDisabled = ChatHolder.message("&cTNT aktualnie jest wyłączone.");

    @Comment("")
    @Comment("<------- Deputy Messages -------> #")
    public SendableMessage deputyMustBeDifferent = ChatHolder.message("&cNie mozesz mianowac siebie zastepca!");
    public SendableMessage deputyRemove = ChatHolder.message("&7Zdegradowno gracza z funkcji &czastepcy&7!");
    public SendableMessage deputyMember = ChatHolder.message("&7Zdegradowano Cie z funkcji &czastepcy&7!");
    public SendableMessage deputySet = ChatHolder.message("&7Ustanowiono nowego &azastepce &7gildii!");
    public SendableMessage deputyOwner = ChatHolder.message("&7Zostales nowym &azastepca &7gildii!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage deputyMembers = ChatHolder.message("&7{PLAYER} zostal nowym &azastepca &7gildii!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage deputyNoLongerMembers = ChatHolder.message("&7{PLAYER} juz nie jest &azastepca &7gildii!");

    @Comment("")
    @Comment("<------- Setbase Messages -------> #")
    public SendableMessage setbaseOutside = ChatHolder.message("&cNie mozna ustawic domu gildii poza jej terenem!");
    public SendableMessage setbaseDone = ChatHolder.message("&7Przeniesiono &adom &7gildii!");

    @Comment("")
    @Comment("<------- PvP Messages -------> #")
    public SendableMessage pvpOn = ChatHolder.message("&cWlaczono pvp w gildii!");
    public SendableMessage pvpOff = ChatHolder.message("&aWylaczono pvp w gildii!");
    @Comment("Dostępne zmienne: {TAG}")
    public SendableMessage pvpAllyOn = ChatHolder.message("&cWlaczono pvp z sojuszem &7{TAG}!");
    public SendableMessage pvpAllyOff = ChatHolder.message("&cWylaczono pvp z sojuszem &7{TAG}!");

    public SendableMessage pvpStatusOn = ChatHolder.message("&aWlaczone");
    public SendableMessage pvpStatusOff = ChatHolder.message("&cWylaczone");

    @Comment("")
    @Comment("<------- Admin Messages -------> #")
    @Comment("Dostępne zmienne: {ADMIN}")
    public SendableMessage adminGuildBroken = ChatHolder.message("&cTwoja gildia zostala rozwiazana przez &7{ADMIN}");
    public SendableMessage adminGuildOwner = ChatHolder.message("&cTen gracz jest zalozycielem gildii, nie mozna go wyrzucic!");
    public SendableMessage adminNoRegionFound = ChatHolder.message("&cGildia nie posiada terenu!");

    public SendableMessage adminNoPointsGiven = ChatHolder.message("&cPodaj liczbe punktow!");
    @Comment("Dostępne zmienne: {ERROR}")
    public SendableMessage adminErrorInNumber = ChatHolder.message("&cNieznana jest liczba: {ERROR}");
    @Comment("Dostępne zmienne: {PLAYER}, {POINTS}, {POINTS-FORMAT}")
    public SendableMessage adminPointsChanged = ChatHolder.message("&aUstawiono &7{POINTS} &apunktow dla gracza &7{PLAYER}");

    public SendableMessage adminNoKillsGiven = ChatHolder.message("&cPodaj liczbe zabojstw!");
    @Comment("Dostępne zmienne: {PLAYER}, {KILLS}")
    public SendableMessage adminKillsChanged = ChatHolder.message("&aUstawiono &7{KILLS} &azabojstw dla gracza &7{PLAYER}");

    public SendableMessage adminNoDeathsGiven = ChatHolder.message("&cPodaj liczbe zgonow!");
    @Comment("Dostępne zmienne: {PLAYER}, {DEATHS}")
    public SendableMessage adminDeathsChanged = ChatHolder.message("&aUstawiono &7{DEATHS} &azgonow dla gracza &7{PLAYER}");

    public SendableMessage adminNoLogoutsGiven = ChatHolder.message("&cPodaj liczbę wylogowań!");

    @Comment("Dostępne zmienne: {PLAYER}, {LOGOUTS}")
    public SendableMessage adminLogoutsChanged = ChatHolder.message("&aUstawiono &7{LOGOUTS} &awylogowań dla gracza &7{PLAYER}");

    public SendableMessage adminNoAssistsGiven = ChatHolder.message("&cPodaj liczbę asyst!");

    @Comment("Dostępne zmienne: {PLAYER}, {ASSISTS}")
    public SendableMessage adminAssistsChanged = ChatHolder.message("&aUstawiono &7{ASSISTS} &aasyst dla gracza &7{PLAYER}");

    public SendableMessage adminNoBanTimeGiven = ChatHolder.message("&cPodaj czas na jaki ma byc zbanowana gildia!");
    public SendableMessage adminNoReasonGiven = ChatHolder.message("&cPodaj powod!");
    public SendableMessage adminGuildBanned = ChatHolder.message("&cTa gildia jest juz zbanowana!");
    public SendableMessage adminTimeError = ChatHolder.message("&cPodano nieprawidlowy czas!");
    @Comment("Dostępne zmienne: {GUILD}, {TIME}")
    public SendableMessage adminGuildBan = ChatHolder.message("&aZbanowano gildie &a{GUILD} &7na okres &a{TIME}&7!");

    public SendableMessage adminGuildNotBanned = ChatHolder.message("&cTa gildia nie jest zbanowana!");
    @Comment("Dostępne zmienne: {GUILD}")
    public SendableMessage adminGuildUnban = ChatHolder.message("&aOdbanowano gildie &7{GUILD}&a!");

    public SendableMessage adminNoLivesGiven = ChatHolder.message("&cPodaj liczbe zyc!");
    @Comment("Dostępne zmienne: {GUILD}, {LIVES}")
    public SendableMessage adminLivesChanged = ChatHolder.message("&aUstawiono &7{LIVES} &azyc dla gildii &7{GUILD}&a!");

    @Comment("Dostępne zmienne: {GUILD}")
    public SendableMessage adminGuildRelocated = ChatHolder.message("&aPrzeniesiono teren gildii &7{GUILD}&a!");

    public SendableMessage adminNoValidityTimeGiven = ChatHolder.message("&cPodaj czas o jaki ma byc przedluzona waznosc gildii!");
    @Comment("Dostępne zmienne: {GUILD}, {VALIDITY}")
    public SendableMessage adminNewValidity = ChatHolder.message("&aPrzedluzono waznosc gildii &a{GUILD} &7do &a{VALIDITY}&7!");

    public SendableMessage adminNoNewNameGiven = ChatHolder.message("&cPodaj nowa nazwe!");
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public SendableMessage adminNameChanged = ChatHolder.message("&aZmieniono nazwe gildii na &7{GUILD}&a!");
    public SendableMessage adminTagChanged = ChatHolder.message("&aZmieniono tag gildii na &7{TAG}&a!");

    public SendableMessage adminStopSpy = ChatHolder.message("&cJuz nie szpiegujesz graczy!");
    public SendableMessage adminStartSpy = ChatHolder.message("&aOd teraz szpiegujesz graczy!");

    public SendableMessage adminGuildsEnabled = ChatHolder.message("&aZakladanie gildii jest wlaczone!");
    public SendableMessage adminGuildsDisabled = ChatHolder.message("&cZakladanie gildii jest wylaczone!");

    public SendableMessage adminUserNotMemberOf = ChatHolder.message("&cTen gracz nie jest czlonkiem tej gildii!");
    public SendableMessage adminAlreadyLeader = ChatHolder.message("&cTen gracz jest juz liderem gildii!");

    public SendableMessage adminNoProtectionDateGive = ChatHolder.message("&cPodaj date ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)");
    public SendableMessage adminInvalidProtectionDate = ChatHolder.message("&cTo nie jest poprawna data! Poprawny format to: yyyy/mm/dd hh:mm:ss");
    public SendableMessage adminProtectionSetSuccessfully = ChatHolder.message("&aPomyslnie ustawiono ochrone dla gildii &7{TAG} &ado &7{DATE}");

    public SendableMessage adminGuildHasNoHome = ChatHolder.message("&cGildia gracza nie ma ustawionej bazy!");
    @Comment("Dostępne zmienne: {ADMIN}")
    public SendableMessage adminTeleportedToBase = ChatHolder.message("&aAdmin &7{ADMIN} &ateleportowal cie do bazy gildii!");
    @Comment("Dostępne zmienne: {PLAYER}")
    public SendableMessage adminTargetTeleportedToBase = ChatHolder.message("&aGracz &7{PLAYER} &azostal teleportowany do bazy gildii!");

    @Comment("")
    @Comment("<------- SecuritySystem Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {CHEAT}, {NOTE}")
    public SendableMessage securitySystemInfo = ChatHolder.message(
            "&8[&4Security&8] &7Gracz &c{PLAYER}&7 może używać &c{CHEAT}&7 lub innego cheata o podobnym dzialaniu!",
            "&8[&4Security&8] &7Notatka: &7{NOTE}"
    );
    @Comment("Dostępne zmienne: {DISTANCE}")
    public String securitySystemReach = "&7Zaatakowal krysztal z odleglosci &c{DISTANCE} &7kratek!";
    @Comment("Dostępne zmienne: {BLOCKS}")
    public String securitySystemFreeCam = "&7Zaatakowal krysztal przez bloki: &c{BLOCKS}";

    @Comment("")
    @Comment("<------- FunnyGuilds Version Messages -------> #")
    public SendableMessage newVersionAvailable = ChatHolder.message(
            "",
            "&8-----------------------------------",
            "&7Dostepna jest nowa wersja &bFunnyGuilds {VERSION_TYPE}&7!",
            "&7Obecna: &b{CURRENT_VERSION}",
            "&7Najnowsza: &b{NEWEST_VERSION}",
            "&7GitHub: &b{GITHUB_LINK}",
            "&7Discord: &b{DISCORD_LINK}",
            "&8-----------------------------------",
            ""
    );

    public SendableMessage funnyguildsVersion = ChatHolder.message("&7FunnyGuilds &b{VERSION} &7by &bFunnyGuilds Team");

    @Comment("")
    @Comment("<------- FunnyBin Messages -------> #")
    public SendableMessage funnybinHelp = ChatHolder.message(
            "&cUzycie:",
            "&c/fg funnybin - domyslnie wysyla FunnyGuilds/config.yml i logs/latest.log",
            "&c/fg funnybin config - wysyla FunnyGuilds/config.yml",
            "&c/fg funnybin log - wysyla logs/latest.log",
            "&c/fg funnybin custom <path> - wysyla dowolny plik z folderu serwera na funnybina",
            "&c/fg funnybin bundle <file1> <fileN...> - wysyla dowolne pliki z folderu serwera na funnybina"
    );

    public SendableMessage funnybinSendingFile = ChatHolder.message("&aWysylam plik: &b{NUM}&a/&b{TOTAL}&a...");
    public SendableMessage funnybinFileNotFound = ChatHolder.message("&cPodany plik: {FILE} nie istnieje");
    public SendableMessage funnybinFileNotOpened = ChatHolder.message("&cPodany plik: {FILE} nie mogl byc otworzony (szczegoly w konsoli)");
    public SendableMessage funnybinFileNotSent = ChatHolder.message("&cPodany plik: {FILE} nie mogl byc wyslany (szczegoly w konsoli)");
    public SendableMessage funnybinFileSent = ChatHolder.message("&aPlik wyslany. Link: &b{LINK}");
    public SendableMessage funnybinBuildingBundle = ChatHolder.message("&aTworze paczke z wyslanych plikow...");
    public SendableMessage funnybinBundleSent = ChatHolder.message("&aPaczka wyslana. Link: &b{LINK}");
    public SendableMessage funnybinBundleNotBuilt = ChatHolder.message("&cWystapil blad podczas tworzenia paczki");

    @Comment("")
    @Comment("<------- System Messages -------> #")
    public SendableMessage reloadTime = ChatHolder.message("&aFunnyGuilds &7przeladowano! (&b{TIME}s&7)");
    public SendableMessage reloadReloading = ChatHolder.message("&7Przeladowywanie...");
    public SendableMessage saveallSaving = ChatHolder.message("&7Zapisywanie...");
    public SendableMessage saveallSaved = ChatHolder.message("&7Zapisano (&b{TIME}s&7)!");
    public String loginNickTooShort = "&cNick jest za krotki!";
    public String loginNickTooLong = "&cNick jest za dlugi!";
    public String loginNickInvalid = "&cNick zawiera niedozwolone znaki!";

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    field.set(this, ChatUtils.colored((String) field.get(this)));
                }

                if (field.getType().equals(List.class)) {
                    List<String> list = (List<String>) field.get(this);
                    list.replaceAll(ChatUtils::colored);
                }
            }
        } catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not load message configuration", ex);
        }

        return this;
    }

}
