package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.MessageRepository;
import dev.peri.yetanothermessageslibrary.message.SendableMessage;
import dev.peri.yetanothermessageslibrary.message.holder.impl.ActionBarHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.BossBarHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.ChatHolder;
import dev.peri.yetanothermessageslibrary.message.holder.impl.TitleHolder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
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

    @Comment("")
    @Comment("<------- Global Date Format -------> #")
    public FunnyTimeFormatter dateFormat = new FunnyTimeFormatter("dd.MM.yyyy HH:mm:ss");

    @Comment("")
    @Comment("<------- No Value Messages -------> #")
    public NoValue noValue = new NoValue();

    public static class NoValue extends OkaeriConfig {

        public Player player = new Player();

        public static class Player extends OkaeriConfig {

            public String top = "Brak (PTOP-x)";

        }

        @Comment("")
        public Guild guild = new Guild();

        public static class Guild extends OkaeriConfig {

            public String name = "Brak (G-NAME/NAME)";
            public String tag = "Brak (G-TAG/TAG)";
            public String owner = "Brak (G-OWNER)";
            public String deputies = "Brak (G-DEPUTIES)";
            public String deputy = "Brak (G-DEPUTY)";
            public String validity = "Brak (G-VALIDITY)";
            public String protection = "Brak (G-PROTECTION)";
            public String regionSize = "Brak (G-REGION-SIZE)";
            public String lives = "Brak (LIVES-SYMBOL/LIVES-SYMBOL-ALL)";
            public String allies = "Brak (ALLIES)";
            public String enemies = "Brak (ENEMIES)";
            public String wgRegionNoValue = "Brak (WG-REGION)";
            public String minMembersToInclude = "Brak (guild-min-members w config.yml)";
            public String top = "Brak (GTOP-x)";

        }

    }



    @Comment("")
    @Comment("<------- General Commands Messages -------> #")
    public Commands commands = new Commands();

    public static class Commands extends OkaeriConfig {

        public Validation validation = new Validation();

        public static class Validation extends OkaeriConfig {

            public SendableMessage playerOnly = ChatHolder.message("&cKomenda dostępna tylko dla graczy!");
            public SendableMessage noPermission = ChatHolder.message("&cNie masz wystarczających uprawnień do wykonania tej komendy!");

            public SendableMessage notOnline = ChatHolder.message("&cTen gracz nie jest obecnie na serwerze!");
            public SendableMessage notPlayedBefore = ChatHolder.message("&cTen gracz nigdy nie byl na serwerze!");

            public SendableMessage noNickGiven = ChatHolder.message("&cPodaj nick gracza!");
            public SendableMessage noNameGiven = ChatHolder.message("&cPodaj nazwę gildii!");
            public SendableMessage noTagGiven = ChatHolder.message("&cPodaj tag gildii!");

            public SendableMessage guildWithTagExist = ChatHolder.message("&cIstniej juz gildia z takim tagiem!");
            public SendableMessage guildWithTagNotExist = ChatHolder.message("&cGildia z takim tagiem nie istnieje!");
            public SendableMessage guildWithNameExists = ChatHolder.message("&cIstnieje już gildia o takiej nazwie!");

            public SendableMessage hasGuild = ChatHolder.message("&cMasz juz gildie!");
            public SendableMessage hasNoGuild = ChatHolder.message("&cNie masz gildii!");
            public SendableMessage userHasGuild = ChatHolder.message("&cTen gracz ma juz gildie!");
            public SendableMessage userHasNoGuild = ChatHolder.message("&cTen gracz nie ma gildii!");
            public SendableMessage notOwner = ChatHolder.message("&cNie jesteś założycielem gildii!");
            public SendableMessage userNotMember = ChatHolder.message("&cTen gracz nie jest członkiem twojej gildii!");

            public SendableMessage invalidNumber = ChatHolder.message("&7Podana wartość &c'{ERROR}' &7nie jest liczbą!");
            public SendableMessage invalidTime = ChatHolder.message("&7Podano nieprawidłowy czas!");
            public SendableMessage invalidDate = ChatHolder.message("&7Podano nieprawidłową datę! Data musi być w formacie: &cyyyy/mm/dd hh:mm:ss&7!");


        }

        @Comment("")
        public Tnt tnt = new Tnt();

        public static class Tnt extends OkaeriConfig {

            public SendableMessage info = ChatHolder.message("&7TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}");
            public SendableMessage infoAlways = ChatHolder.message("&7TNT wybucha o każdej porze.");
            public SendableMessage enabled = ChatHolder.message("&aTNT aktualnie jest włączone.");
            public SendableMessage disabled = ChatHolder.message("&cTNT aktualnie jest wyłączone.");

        }

    }

    @Comment("")
    @Comment("<------- Player Messages -------> #")
    public Player player = new Player();

    public static class Player extends OkaeriConfig {

        public Commands commands = new Commands();

        public static class Commands extends OkaeriConfig {

            public Info info = new Info();

            public static class Info extends OkaeriConfig {

                public SendableMessage longForm = ChatHolder.message(
                        "&8--------------------------------",
                        "&7Gracz: &a{PLAYER}",
                        "&7Gildia: &a{TAG}",
                        "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
                        "&7Zabójstwa: &a{KILLS}",
                        "&7Śmierci: &a{DEATHS}",
                        "&7Asysty: &a{ASSISTS}",
                        "&7Wylogowania: &a{LOGOUTS}",
                        "&7KDR: &a{KDR}",
                        "&8--------------------------------"
                );

                public SendableMessage shortForm = ChatHolder.message(
                        "&8--------------------------------",
                        "&7Gracz: &a{PLAYER}",
                        "&7Gildia: &a{TAG}",
                        "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
                        "&8--------------------------------"
                );

            }

            @Comment("")
            @Comment("{PTOP-<typ>-<pozycja>} - gracz na podanej pozycji w topce dla danego typu. Lista dostępnych typów znajduje się w 'config.yml' pod kluczem 'top.enabled-user-tops'")
            public SendableMessage topList = ChatHolder.message(
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
            public RankReset rankReset = new RankReset();

            public static class RankReset extends OkaeriConfig {

                @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
                public SendableMessage missingItems = ChatHolder.message("&cNie masz wszystkich przedmiotów! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
                @Comment("Dostępne zmienne: {LAST-RANK}, {CURRENT-RANK}")
                public SendableMessage resetMessage = ChatHolder.message("&7Zresetowałeś swój ranking z poziomu &c{LAST-RANK} &7do poziomu &c{CURRENT-RANK}&7.");

            }

            @Comment("")
            public StatsReset statsReset = new StatsReset();

            public static class StatsReset extends OkaeriConfig {

                @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
                public SendableMessage missingItems = ChatHolder.message("&cNie masz wszystkich przedmiotów! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
                @Comment("Dostępne zmienne: {LAST-POINTS}, {CURRENT-POINTS}, {LAST-KILLS}, {CURRENT-KILLS}, {LAST-DEATHS}, {CURRENT-DEATHS}, {LAST-ASSISTS}, {CURRENT-ASSISTS}, {LAST-LOGOUTS}, {CURRENT-LOGOUTS}")
                public SendableMessage resetMessage = ChatHolder.message(
                        "&7Zresetowałeś swoje statystyki do podstawowych",
                        " &7Punkty: &c{LAST-POINTS} &8-> &a{CURRENT-POINTS}",
                        " &7Zabójstwa: &c{LAST-KILLS} &8-> &a{CURRENT-KILLS}",
                        " &7Śmierci: &c{LAST-DEATHS} &8-> &a{CURRENT-DEATHS}",
                        " &7Asysty: &c{LAST-ASSISTS} &8-> &a{CURRENT-ASSISTS}",
                        " &7Wylogowania: &c{LAST-LOGOUTS} &8-> &a{CURRENT-LOGOUTS}"
                );

            }

        }

        @Comment("")
        public Rank rank = new Rank();

        public static class Rank extends OkaeriConfig {

            public Farming farming = new Farming();

            public static class Farming extends OkaeriConfig {

                public Message lastVictim = new Message(
                        "&7Ostatnio zostałeś zabity przez tego samego gracza, punkty nie zostają odebrane!",
                        "&7Ostatnio zabiłeś tego samego gracza, punkty nie zostają dodane!"
                );

                public Message lastAttacker = new Message(
                        "&7Ostatnio zostałeś zabity przez tego samego gracza, punkty nie zostają dodane!",
                        "&7Ostatnio zabiłeś tego samego gracza, punkty nie zostają odebrane!"
                );

                public Message sameIP = new Message(
                        "&7Ten gracz ma taki sam adres IP, punkty nie zostają odebrane!",
                        "&7Ten gracz ma taki sam adres IP, punkty nie zostają dodane!"
                );

                public Message sameGuild = new Message(
                        "&7Ten gracz jest w twojej gildii, punkty nie zostają odebrane!",
                        "&7Ten gracz jest w twojej gildii, punkty nie zostają dodane!"
                );

                public Message sameAlliance = new Message(
                        "&7Ten gracz jest w sojuszu z twoją gildią, punkty nie zostają odebrane!",
                        "&7Ten gracz jest w sojuszu z twoją gildią, punkty nie zostają dodane!"
                );

                public static class Message extends OkaeriConfig {

                    public SendableMessage victim;
                    public SendableMessage attacker;

                    private Message(SendableMessage victim, SendableMessage attacker) {
                        this.victim = victim;
                        this.attacker = attacker;
                    }

                    public Message(String victim, String attacker) {
                        this.victim = ChatHolder.message(victim);
                        this.attacker = ChatHolder.message(attacker);
                    }

                }

            }

            @Comment("")
            @Comment("Dostępne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {MINUS-FORMATTED}, {PLUS-FORMATTED}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {WEAPON-NAME}, {ITEM}, {ITEM-NO-AMOUNT}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
            public PvP pvp = new PvP();

            public static class PvP extends OkaeriConfig {

                public SendableMessage broadcast = ChatHolder.message("{ATAG}&b{ATTACKER} &7({PLUS-FORMATTED}&7) zabił {VTAG}&b{VICTIM} &7({MINUS-FORMATTED}&7) używając &b{ITEM-NO-AMOUNT}");
                public SendableMessage killer = TitleHolder.message("&cZabiłeś gracza {VICTIM}", "&7{PLUS-FORMATTED}", 10, 10, 10);
                public SendableMessage victim = TitleHolder.message("&cZostałeś zabity przez gracza {ATTACKER}", "&7{MINUS-FORMATTED}", 10, 10, 10);

                public Assists assists = new Assists();

                public static class Assists extends OkaeriConfig {

                    @Comment("Zamiast zmiennej {ASSISTS} wstawiane są kolejne wpisy o asystujących graczach")
                    public String message = "&7Asystowali: {ASSISTS}";

                    @Comment("Dostępne zmienne: {PLAYER}, {+}, {PLUS-FORMATTED}, {SHARE}")
                    public String entry = "&b{PLAYER} &7({PLUS-FORMATTED}&7, {SHARE}% dmg)";

                    @Comment("Znaki oddzielające kolejne wpisy o asystujących graczach")
                    public String delimiter = "&8, ";

                }

            }

        }

    }


    @Comment("")
    @Comment("<------- Player Messages -------> #")
    public Guild guild = new Guild();

    public static class Guild extends OkaeriConfig {

        public Commands commands = new Commands();

        public static class Commands extends OkaeriConfig {

            public Validation validation = new Validation();

            public static class Validation extends OkaeriConfig {

                public SendableMessage hasNoRegion = ChatHolder.message("&cTwoja gildia nie posiada terenu!");
                public SendableMessage hasNoCenter = ChatHolder.message("&cTwoja gildia nie posiada srodka regionu!");

                public SendableMessage notAllied = ChatHolder.message("&cNie posiadasz sojuszu z ta gildia!");

            }

            @Comment("")
            public SendableMessage help = ChatHolder.message(
                    "&7---------------------&8[ &aGildie &8]&7---------------------",
                    "&a/zaloz [tag] [nazwa] &8- &7Tworzy gildie",
                    "&a/zapros [gracz] &8- &7Zaprasza gracza do gildii",
                    "&a/dolacz [tag] &8- &7Przyjmuje zaproszenie do gildii",
                    "&a/info [tag] &8- &7Informacje o danej gildii",
                    "&a/baza &8- &7Teleportuje do bazy gildii",
                    "&a/powieksz &8- &7Powiększa teren gildii",
                    "&a/przedluz &8- &7Przedłuża ważność gildii",
                    "&a/lider [gracz] &8- &7Oddaje założyciela gildii",
                    "&a/zastepca [gracz] &8- &7Nadaje zastępce gildii",
                    "&a/sojusz [tag] &8- &7Pozwala nawiązać sojusz",
                    "&a/opusc &8- &7Opuszcza gildie",
                    "&a/wyrzuc [gracz] &8- &7Wyrzuca gracza z gildii",
                    "&a/rozwiaz [tag] &8- &7Rozwiązuje sojusz",
                    "&a/usun &8- &7Usuwa gildie",
                    "&a/przedmioty &8- &7Pokazuje przedmioty potrzebne do założenia gildii",
                    "&a/ucieczka &8- &7Rozpoczyna ucieczkę z terenu innej gildii"
            );

            @Comment("")
            @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {AVG-POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
            public SendableMessage info = ChatHolder.message(
                    "&8-------------------------------",
                    "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
                    "&7Zalozyciel: &c{OWNER}",
                    "&7Zastepcy: &c{DEPUTIES}",
                    "&7Punkty: &c{POINTS} &8[&c{G-POSITION-AVG_POINTS}&8]",
                    "&7Ochrona: &c{PROTECTION}",
                    "&7Zycia: &4{LIVES}",
                    "&7Waznosc: &c{VALIDITY}",
                    "&7Czlonkowie: &7{MEMBERS}",
                    "&7Sojusze: &c{ALLIES}",
                    "&7Wojny: &c{ENEMIES}",
                    "&8-------------------------------"
            );

            @Comment("")
            public Create create = new Create();

            public static class Create extends OkaeriConfig {

                @Comment("Dostępne zmienne: {LENGTH}")
                public SendableMessage tagMinLength = ChatHolder.message("&7Tag nie może byc krótszy niz &c{LENGTH} litery&7!");
                @Comment("Dostępne zmienne: {LENGTH}")
                public SendableMessage tagMaxLength = ChatHolder.message("&7Tag nie może byc dłuższy niz &c{LENGTH} litery&7!");
                @Comment("Dostępne zmienne: {LENGTH}")
                public SendableMessage nameMinLength = ChatHolder.message("&cNazwa nie może byc krótsza niz &c{LENGTH} litery&7!");
                @Comment("Dostępne zmienne: {LENGTH}")
                public SendableMessage nameMaxLength = ChatHolder.message("&cNazwa nie może byc dłuższa niz &c{LENGTH} litery&7!");
                public SendableMessage invalidTag = ChatHolder.message("&cTag gildii może zawierać tylko litery!");
                public SendableMessage invalidName = ChatHolder.message("&cNazwa gildii może zawierać tylko litery!");
                public SendableMessage restrictedGuildTag = ChatHolder.message("&cPodany tag gildii jest niedozwolony.");
                public SendableMessage restrictedGuildName = ChatHolder.message("&cPodana nazwa gildii jest niedozwolona.");

                public SendableMessage blockedWorld = ChatHolder.message("&cTworzenie gildii jest zablokowane na tym swiecie!");
                public SendableMessage invalidLocation = ChatHolder.message("&cNie możesz stworzyć gildii w tym miejscu!");
                @Comment("Dostępne zmienne: {DISTANCE}")
                public SendableMessage nearSpawn = ChatHolder.message("&7Jesteś zbyt blisko spawnu! Minimalna odległość to &c{DISTANCE}");
                public SendableMessage nearOtherGuild = ChatHolder.message("&cW pobliżu znajduje sie jakaś gildia, poszukaj innego miejsca!");
                @Comment("Dostępne zmienne: {BORDER-MIN-DISTANCE}")
                public SendableMessage nearBorder = ChatHolder.message("&cJesteś zbyt blisko granicy mapy aby założyć gildie! (Minimalna odległość: {BORDER-MIN-DISTANCE})");

                @Comment("Dostępne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
                public SendableMessage missingRankingPoints = ChatHolder.message("&cAby założyć gildie, wymagane jest przynajmniej &7{REQUIRED} &cpunktów.");
                @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
                public SendableMessage missingItems = ChatHolder.message("&cNie masz wszystkich przedmiotów! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}&c. Najedz na przedmiot, aby dowiedzieć sie więcej.");
                @Comment("Dostępne zmienne: {EXP}")
                public SendableMessage missingExperience = ChatHolder.message("&cNie posiadasz wymaganego doświadczenia do założenia gildii: &7{EXP}");
                @Comment("Dostępne zmienne: {MONEY}")
                public SendableMessage missingMoney = ChatHolder.message("&cNie posiadasz wymaganej ilości pieniędzy do załozenia gildii: &7{MONEY}");
                public SendableMessage withdrawError = ChatHolder.message("&cNie udało sie pobrać pieniędzy z twojego konta z powodu: &7{ERROR}");

                public SendableMessage couldNotPasteSchematic = ChatHolder.message("&cWystąpił błąd podczas tworzenia terenu gildii, zgloś sie do administracji.");

                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage created = ChatHolder.message("&7Założyłeś gildię o nazwie &a{GUILD} &7i tagu &a{TAG}&7!");

                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage createdBroadcast = ChatHolder.message("&7Gracz &a{PLAYER} &7założył gildię o nazwie &a{GUILD} &7i tagu &a{TAG}&7!");

            }

            @Comment("")
            public Delete delete = new Delete();

            public static class Delete extends OkaeriConfig {

                public SendableMessage someoneNearby = ChatHolder.message("&cNie możesz usunąć gildii, ktoś jest w pobliżu!");
                public SendableMessage confirm = ChatHolder.message("&7Aby potwierdzić usunięcie gildii, wpisz: &c/potwierdz");
                public SendableMessage notingToConfirm = ChatHolder.message("&cNie masz żadnych działań do potwierdzenia!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage deleted = ChatHolder.message("&7Pomyślnie &cusunięto &7gildie!");
                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage deletedBroadcast = ChatHolder.message("&c{PLAYER} &7rozwiązał gildie &c{TAG}&7!");

            }

            @Comment("")
            public Enlarge enlarge = new Enlarge();

            public static class Enlarge extends OkaeriConfig {

                public SendableMessage maxSize = ChatHolder.message("&cOsiagnieto juz maksymalny rozmiar terenu!");
                public SendableMessage nearOtherGuild = ChatHolder.message("&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!");

                @Comment("Dostępne zmienne: {ITEM}")
                public SendableMessage missingItems = ChatHolder.message("&7Nie masz wystarczajacej liczby przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");

                @Comment("Dostępne zmienne: {SIZE}, {LEVEL}")
                public SendableMessage enlarged = ChatHolder.message("&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)");

            }

            @Comment("")
            public Validity validity = new Validity();

            public static class Validity extends OkaeriConfig {

                @Comment("Dostępne zmienne: {TIME}")
                public SendableMessage tooEarly = ChatHolder.message("&7Gildie możesz przedłużyć dopiero za &c{TIME}&7!");
                @Comment("Dostępne zmienne: {ITEM}")
                public SendableMessage missingItems = ChatHolder.message("&7Nie masz wystarczającej liczby przedmiotów! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");

                @Comment("Dostępne zmienne: {DATE}")
                public SendableMessage extended = ChatHolder.message("&7Ważność gildii przedłużona do &a{DATE}&7!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
                public SendableMessage expiredBroadcast = ChatHolder.message("&7Gildia &b{TAG} &7wygasła&b! &7Jej baza znajdowała ęie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!");

                public String noCoordinates = "N/A";

            }

            @Comment("")
            public Invite invite = new Invite();

            public static class Invite extends OkaeriConfig {

                @Comment("Dostępne zmienne: {AMOUNT}")
                public SendableMessage playersLimit = ChatHolder.message("&7Osiągnięto juz &cmaksymalną &7liczbę członków w gildii! (&c{AMOUNT}&7)");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage cancelled = ChatHolder.message("&cCofnięto zaproszenie dla {PLAYER}!");
                @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
                public SendableMessage cancelledTarget = ChatHolder.message("&7Zaproszenie do gildii &c{GUILD} &7zostało wycofane!");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage invited = ChatHolder.message("&7Gracz &a{PLAYER} &7został zaproszony do gildii!");
                @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
                public SendableMessage invitedTarget = ChatHolder.message("&aOtrzymano zaproszenie do gildii &7{TAG}&a!");

                @Comment("")
                public All all = new All();

                public static class All extends OkaeriConfig {

                    @Comment("Dostępne zmienne: {ERROR}")
                    public SendableMessage invalidNumber = ChatHolder.message("&7Zasięg &c{ERROR} &7nie jest poprawną liczbą!");
                    @Comment("Dostępne zmienne: {MAX_RANGE}")
                    public SendableMessage rangeToBig = ChatHolder.message("&7Podany zasięg jest zbyt duży! (Maksymalnie: &c{MAX_RANGE}&7)");
                    public SendableMessage noOneNearby = ChatHolder.message("&cNikogo nie ma w pobliżu!");

                    @Comment("Dostępne zmienne: {RANGE}")
                    public SendableMessage invited = ChatHolder.message("&aZapraszasz wszystkich w promieniu {RANGE} bloków!");

                }

            }

            @Comment("")
            public Join join = new Join();

            public static class Join extends OkaeriConfig {

                public SendableMessage noInvitations = ChatHolder.message("&cNie masz zaproszenia do gildii!");
                @Comment("Dostępne zmienne: {GUILDS}")
                public SendableMessage invitationsList = ChatHolder.message(
                        "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
                        "&7Wpisz &a/dolacz [tag] &7aby dołączyć do wybranej gildii"
                );

                public SendableMessage noInvitationGuild = ChatHolder.message("&cNie otrzymałeś zaproszenia do tej gildii!");
                public SendableMessage playerLimit = ChatHolder.message("&7Ta gildia osiągneła juz &cmaksymalną &7liczbę członków! (&c{AMOUNT}&7)");
                @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
                public SendableMessage missingItems = ChatHolder.message("&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");

                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage joined = ChatHolder.message("&a{PLAYER} &7dołączył do &aTwojej &7gildii!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage joinedTarget = ChatHolder.message("&aDołaczyłeś do gildii &7{GUILD}");
                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage joinedBroadcast = ChatHolder.message("&a{PLAYER} &7dołączył do gildii &a{TAG}&7!");

            }

            @Comment("")
            public Leave leave = new Leave();

            public static class Leave extends OkaeriConfig {

                public SendableMessage youAreOwner = ChatHolder.message("&cZałożyciel &7nie może opuścić gildii!");

                @Comment("Dostępne zmienne: {GUILDS}, {TAG}")
                public SendableMessage left = ChatHolder.message("&7Opuściłeś gildie &a{GUILD}&7!");
                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage leftBroadcast = ChatHolder.message("&c{PLAYER} &7opuścił gildie &c{TAG}&7!");

            }

            @Comment("")
            public Kick kick = new Kick();

            public static class Kick extends OkaeriConfig {

                public SendableMessage targetIsOwner = ChatHolder.message("&cNie można wyrzucić założyciela!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}, {PLAYER}")
                public SendableMessage kicked = ChatHolder.message("&c{PLAYER} &7został wyrzucony z gildii!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage kickedTarget = ChatHolder.message("&cZostałeś wyrzucony z gildii!");
                @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
                public SendableMessage kickedBroadcast = ChatHolder.message("&c{PLAYER} &7został &cwyrzucony &7z gildii &c{TAG}&7!");

            }

            @Comment("")
            public Leader leader = new Leader();

            public static class Leader extends OkaeriConfig {

                public SendableMessage leaderMustBeDifferent = ChatHolder.message("&cJuż jesteś liderem swojej gildii!");

                public SendableMessage changed = ChatHolder.message("&7Ustanowiono nowego &alidera &7gildii!");
                public SendableMessage changedTarget = ChatHolder.message("&7Zostales nowym &aliderem &7gildii!");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage changedMembers = ChatHolder.message("&7{PLAYER} zostal nowym &aliderem &7gildii!");

            }

            @Comment("")
            public Deputy deputy = new Deputy();

            public static class Deputy extends OkaeriConfig {

                public SendableMessage deputyMustBeDifferent = ChatHolder.message("&cNie mozesz mianowac siebie zastepca!");

                public SendableMessage removed = ChatHolder.message("&7Zdegradowno gracza z funkcji &czastepcy&7!");
                public SendableMessage removedTarget = ChatHolder.message("&7Zdegradowano Cie z funkcji &czastepcy&7!");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage removedMembers = ChatHolder.message("&7{PLAYER} juz nie jest &azastepca &7gildii!");

                public SendableMessage set = ChatHolder.message("&7Ustanowiono nowego &azastepce &7gildii!");
                public SendableMessage setTarget = ChatHolder.message("&7Zostales nowym &azastepca &7gildii!");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage setMembers = ChatHolder.message("&7{PLAYER} zostal nowym &azastepca &7gildii!");

            }

            @Comment("")
            public Ally ally = new Ally();

            public static class Ally extends OkaeriConfig {

                public SendableMessage alliesLimit = ChatHolder.message("&7Osiągnięto juz &cmaksymalną &7liczbę sojuszy między gildyjnych! (&c{AMOUNT}&7)");
                @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
                public SendableMessage targetAlliesLimit = ChatHolder.message("&7Gildia {TAG} posiada juz maksymalna liczbę sojuszy! (&c{AMOUNT}&7)");

                public SendableMessage yourGuild = ChatHolder.message("&cNie możesz nawiązać sojuszu z własną gildia!");
                @Comment("Dostępne zmienne: {TAG}")
                public SendableMessage alreadyAllied = ChatHolder.message("&cMasz już sojusz z tą gildią!");

                public SendableMessage noInvitations = ChatHolder.message("&7Aby zaprosić gildie do sojuszy wpisz &c/sojusz [tag]");
                @Comment("Dostępne zmienne: {GUILDS}")
                public SendableMessage invitationsList = ChatHolder.message(
                        "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
                        "&7Aby zaakceptować użyj &a/sojusz [tag]"
                );

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage allyInvite = ChatHolder.message("&7Zaproszono gildie &a{GUILD} &7do sojuszu!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage allyInviteTarget = ChatHolder.message("&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage allyInviteReturn = ChatHolder.message("&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage allyInviteReturnTarget = ChatHolder.message("&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage allied = ChatHolder.message("&7Nawiązano sojusz z gildia &a{GUILD}&7!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage alliedTarget = ChatHolder.message("&7Gildia &a{GUILD} &7przystaąila do sojuszu!");

            }

            @Comment("")
            @CustomKey("break")
            public Break breakAlly = new Break();

            public static class Break extends OkaeriConfig {

                public SendableMessage noAllies = ChatHolder.message("&cTwoja gildia nie posiada sojuszy!");
                @Comment("Dostępne zmienne: {GUILDS}")
                public SendableMessage alliesList = ChatHolder.message(
                        "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
                        "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]"
                );

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage broke = ChatHolder.message("&7Rozwiazano sojusz z gildia &c{GUILD}&7!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage brokeTarget = ChatHolder.message("&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!");

            }

            @Comment("")
            public Enemy enemy = new Enemy();

            public static class Enemy extends OkaeriConfig {

                public SendableMessage correctUsage = ChatHolder.message("&7Aby rozpoczac wojne z gildia wpisz &c/wojna [tag]");

                @Comment("Dostępne zmienne: {AMOUNT}")
                public SendableMessage enemiesLimit = ChatHolder.message("&7Osiagnieto juz &cmaksymalna &7liczbe wojen miedzygildyjnych! (&c{AMOUNT}&7)");
                @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
                public SendableMessage targetEnemiesLimit = ChatHolder.message("&7Gildia {TAG} posiada juz maksymalna liczbe wojen! (&c{AMOUNT}&7)");

                public SendableMessage yourGuild = ChatHolder.message("&cNie mozesz rozpoczac wojny z wlasna gildia!");
                public SendableMessage targetIsAlly = ChatHolder.message("&cNie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!");
                public SendableMessage alreadyEnemy = ChatHolder.message("&cProwadzisz juz wojne z ta gildia!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage enemy = ChatHolder.message("&7Wypowiedziano gildii &a{GUILD}&7 wojne!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage enemyTarget = ChatHolder.message("&7Gildia &a{GUILD} &7wypowiedziala twojej gildii wojne!");

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage enemyEnd = ChatHolder.message("&7Zakonczono wojne z gildia &a{GUILD}&7!");
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage enemyEndTarget = ChatHolder.message("&7Gildia &a{GUILD} &7zakonczyla wojne z twoja gildia!");

            }

            @Comment("")
            public PvP pvp = new PvP();

            public static class PvP extends OkaeriConfig {

                public SendableMessage allyPvPDisabled = ChatHolder.message("&cPVP pomiedzy sojuszami jest wylaczone w konfiguracji!");

                public SendableMessage enabled = ChatHolder.message("&cWlaczono pvp w gildii!");
                public SendableMessage disabled = ChatHolder.message("&aWylaczono pvp w gildii!");
                @Comment("Dostępne zmienne: {TAG}")
                public SendableMessage enabledAlly = ChatHolder.message("&cWlaczono pvp z sojuszem &7{TAG}!");
                public SendableMessage disabledAlly = ChatHolder.message("&cWylaczono pvp z sojuszem &7{TAG}!");

                public String enabledStatus = "&aWłączone";
                public String disabledStatus = "&cWyłączone";

            }

            @Comment("")
            public SetBase setBase = new SetBase();

            public static class SetBase extends OkaeriConfig {

                public SendableMessage outsideRegion = ChatHolder.message("&cNie mozna ustawic domu gildii poza jej terenem!");
                public SendableMessage set = ChatHolder.message("&7Przeniesiono &adom &7gildii!");

            }

            @Comment("")
            public Base base = new Base();

            public static class Base extends OkaeriConfig {

                public SendableMessage disabled = ChatHolder.message("&cTeleportacja do baz gildyjnych nie jest dostępna");
                @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
                public SendableMessage missingItems = ChatHolder.message("&cNie masz wszystkich przedmiotów! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}");
                public SendableMessage alreadyTeleporting = ChatHolder.message("&cWłaśnie sie teleportujesz!");

                public SendableMessage teleporting = ChatHolder.message("&7Nie ruszaj sie przez &c{TIME} &7sekund!");
                public SendableMessage cancelled = ChatHolder.message("&cRuszyłeś sie, teleportacja przerwana!");
                public SendableMessage teleported = ChatHolder.message("&aTeleportacja&7...");

            }

            @Comment("Dostępne zmienne: {PLAYER}, {X}, {Y}, {Z}")
            public SendableMessage helpRequest = SendableMessage.builder()
                    .chat("&7Gracz &a{PLAYER} &7poprosił o pomoc&7! &aX: {X} Y: {Y} Z: {Z}")
                    .actionBar("&7Gracz &a{PLAYER} &7poprosił o pomoc&7! &aX: {X} Y: {Y} Z: {Z}")
                    .build();

            @Comment("")
            public Escape escape = new Escape();

            public static class Escape extends OkaeriConfig {

                public SendableMessage disabled = ChatHolder.message("&cPrzykro mi, ucieczki sa wyłączone!");
                public SendableMessage alreadyEscaping = ChatHolder.message("&cUcieczka juz trwa!");

                public SendableMessage notInRegion = ChatHolder.message("&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?");
                public SendableMessage noGuild = ChatHolder.message("&cNie masz gildii do ktorej moglbys uciekac!");
                public SendableMessage yourRegion = ChatHolder.message("&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?");

                @Comment("Dostępne zmienne: {TIME}")
                public SendableMessage escaping = ChatHolder.message("&aDobrze, jeśli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!");
                @Comment("Dostępne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
                public SendableMessage escapingOpponents = ChatHolder.message("&cGracz {PLAYER} próbuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})");
                public SendableMessage cancelled = ChatHolder.message("&cUcieczka został przerwana!");
                public SendableMessage escaped = ChatHolder.message("&aUdalo ci sie uciec!");
                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage escapedOpponents = ChatHolder.message("&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!");

            }

            @Comment("")
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

        }

        @Comment("")
        public War war = new War();

        public static class War extends OkaeriConfig {

            public SendableMessage disabled = ChatHolder.message("&cPodbijanie gildii jest wyłączone.");
            public SendableMessage hasNoGuild = ChatHolder.message("&cMusisz miec gildie, aby zaatkowac inna!");
            public SendableMessage guildIsAlly = ChatHolder.message("&cNie mozesz zaatakowac sojusznika!");
            @Comment("Dostępne zmienne: {TIME}")
            public SendableMessage guildHasProtection = ChatHolder.message("&7Atak na gildie mozliwy za &4{TIME}");

            @Comment("Dostępne zmienne: {ATTACKED}")
            public SendableMessage attacked = ChatHolder.message("&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!");
            @Comment("Dostępne zmienne: {ATTACKER}")
            public SendableMessage attackedTarget = ChatHolder.message("&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!");

            @Comment("Dostępne zmienne: {LOSER}")
            public SendableMessage win = ChatHolder.message("&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!");
            @Comment("Dostępne zmienne: {WINNER}")
            public SendableMessage lose = ChatHolder.message("&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!");
            @Comment("Dostępne zmienne: {WINNER}, {LOSER}")
            public SendableMessage conqueredBroadcast = ChatHolder.message("&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!");

        }

        @Comment("")
        public Region region = new Region();

        public static class Region extends OkaeriConfig {

            public SendableMessage disabled = ChatHolder.message("&cRegiony gildii sa wyłączone!");

            @Comment("")
            public Protection protection = new Protection();

            public static class Protection extends OkaeriConfig {

                public SendableMessage unauthorized = ChatHolder.message("&cTen teren name do innej gildii!");
                public SendableMessage center = ChatHolder.message("&cNie możesz zniszczyć środka swojej gildii!");
                public SendableMessage heart = ChatHolder.message("&cNie możesz ingerować w okolice serca swojej gildii!");
                public SendableMessage other = ChatHolder.message("&cNie możesz tego zrobić na terenie gildii!!");
                public SendableMessage command = ChatHolder.message("&cNie możesz użyć tej komendy na terenie gildii!");
                public SendableMessage teleport = ChatHolder.message("&cNie możesz się teleportować na teren tej gildii!");

            }

            @Comment("")
            public Explosion explosion = new Explosion();

            public static class Explosion extends OkaeriConfig {

                @Comment("Dostępne zmienne: {TIME}")
                public SendableMessage message = ChatHolder.message("&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!");

                public SendableMessage hasProtection = ChatHolder.message("&cEksplozja nie spowodowała zniszczeń na terenie gildii, ponieważ jest ona chroniona!");

                @Comment("Dostępne zmienne: {TIME}")
                public SendableMessage interaction = ChatHolder.message("&cNie monad budować jeszcze przez &4{TIME} sekund&c!");

            }

            @Comment("")
            public Move move = new Move();

            public static class Move extends OkaeriConfig {

                @Comment("Dostępne zmienne: {PLAYER}")
                public SendableMessage intruderEnter = SendableMessage.builder()
                        .actionBar("&7Gracz &c{PLAYER} &7wkroczył na teren &cTwojej &7gildii!")
                        .addHolders(BossBarHolder.builder("&7Gracz &c{PLAYER} &7wkroczył na teren &cTwojej &7gildii!")
                                .color(BossBar.Color.RED)
                                .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                                .stay(15 * 20)
                                .clearOtherBars(true)
                                .build())
                        .build();

                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage enter = SendableMessage.builder()
                        .actionBar("&7Wkroczyłeś na teren gildii &c{TAG}&7!")
                        .addHolders(BossBarHolder.builder("&7Wkroczyłeś na teren gildii &c{TAG}&7!")
                                .color(BossBar.Color.RED)
                                .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                                .stay(15 * 20)
                                .clearOtherBars(true)
                                .build())
                        .build();
                @Comment("Dostępne zmienne: {GUILD}, {TAG}")
                public SendableMessage leave = SendableMessage.builder()
                        .actionBar("&7Opuściłeś teren gildii &c{TAG}&7!")
                        .addHolders(BossBarHolder.builder("&7Opuściłeś teren gildii &c{TAG}&7!")
                                .color(BossBar.Color.GREEN)
                                .overlay(BossBar.Overlay.PROGRESS)
                                .addFlag(BossBar.Flag.CREATE_WORLD_FOG)
                                .stay(15 * 20)
                                .clearOtherBars(true)
                                .build())
                        .build();

            }

        }

    }

    @Comment("")
    @Comment("<------- Adnmin Messages -------> #")
    public Admin admin = new Admin();

    public static class Admin extends OkaeriConfig {

        public Commands commands = new Commands();

        public static class Commands extends OkaeriConfig {

            public Validation validation = new Validation();

            public static class Validation extends OkaeriConfig {

                public SendableMessage notMemberOf = ChatHolder.message("&cTen gracz nie jest czlonkiem tej gildii!");

                public SendableMessage noRegion = ChatHolder.message("&cGildia nie posiada terenu!");

            }

            @Comment("")
            public SendableMessage help = ChatHolder.message(
                    "&a/ga points [nick] [points] &8- &7Ustawia liczbę punktów gracza",
                    "&a/ga kills [nick] [kills] &8- &7Ustawia liczbę zabójstw gracza",
                    "&a/ga deaths [nick] [deaths] &8- &7Ustawia liczbę śmierci gracza",
                    "&a/ga assists [nick] [assist]s &8- &7Ustawia liczbę asyst gracza",
                    "&a/ga logouts [nick] [logouts] &8- &7Ustawia liczbę wylogowań gracza",
                    "&a/ga status &8- &7Zarządzanie statusem zakładania gildii",
                    "&a/ga usun [tag] &8- &7Usuwa gildie",
                    "&a/ga tag [tag] [nowy tag] &8- &7Zmienia tag gildii",
                    "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwę gildii",
                    "&a/ga przedluz [tag] [czas] &8- &7Przedluza waznosc gildii o podany czas",
                    "&a/ga ochrona [tag] [czas] &8- &7Ustawia date wygasniecia ochrony",
                    "&a/ga zycia [tag] [życia] &8- &7Ustawia liczbę zyc gildii",
                    "&a/ga przenies [tag] &8- &7Przenosi teren gildii",
                    "&a/ga tp [tag] &8- &7Teleportuje do bazy gildii",
                    "&a/ga baza [gracz] &8- &7Teleportuje gracza do bazy jego gildii",
                    "&a/ga dodaj [tag] [nick] &8- &7Dodaje gracza do gildii",
                    "&a/ga wyrzuc [nick] &8- &7Wyrzuca gracza z gildii",
                    "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
                    "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastępce gildii",
                    "&a/ga ban [tag] [czas] [powód] &8- &7Banuje gildie na określony czas",
                    "&a/ga unban [tag] &8- &7Odbanowywuje gildie",
                    "&a/ga spy &8- &7Szpieguje czat gildii"
            );

            @Comment("")
            public Player player = new Player();

            public static class Player extends OkaeriConfig {

                public StatsChange points = new StatsChange(
                        "&cPodaj liczbę punktów!",
                        "&7Zmieniłeś liczbę punktów gracza &c{PLAYER} &7na &c{VALUE}&7!"
                );

                @Comment("")
                public StatsChange kills = new StatsChange(
                        "&cPodaj liczbę zabójstw!",
                        "&7Zmieniłeś liczbę zabójstw gracza &c{PLAYER} &7na &c{VALUE}&7!"
                );

                @Comment("")
                public StatsChange deaths = new StatsChange(
                        "&cPodaj liczbę śmierci!",
                        "&7Zmieniłeś liczbę śmierci gracza &c{PLAYER} &7na &c{VALUE}&7!"
                );

                @Comment("")
                public StatsChange assists = new StatsChange(
                        "&cPodaj liczbę asyst!",
                        "&7Zmieniłeś liczbę asyst gracza &c{PLAYER} &7na &c{VALUE}&7!"
                );

                @Comment("")
                public StatsChange logouts = new StatsChange(
                        "&cPodaj liczbę wylogowań!",
                        "&7Zmieniłeś liczbę wylogowań gracza &c{PLAYER} &7na &c{VALUE}&7!"
                );

                public static class StatsChange extends OkaeriConfig {

                    public SendableMessage noValueGiven;
                    @Comment("Dostępne zmienne: {PLAYER}, {VALUE}")
                    public SendableMessage changed;

                    public StatsChange(String noValueGiven, String changed) {
                        this.noValueGiven = ChatHolder.message(noValueGiven);
                        this.changed = ChatHolder.message(changed);
                    }

                }

            }

            @Comment("")
            public Guild guild = new Guild();

            public static class Guild extends OkaeriConfig {

                public Status status = new Status();

                public static class Status extends OkaeriConfig {

                    public SendableMessage enabled = ChatHolder.message("&aZakładanie gildii jest włączone!");
                    public SendableMessage disabled = ChatHolder.message("&cZakładanie gildii jest wyłączone!");

                }

                @Comment("")
                public Delete delete = new Delete();

                public static class Delete extends OkaeriConfig {

                    @Comment("Dostępne zmienne: {ADMIN}")
                    public SendableMessage deletedOwner = ChatHolder.message("&7Twoja gildia została rozwiązana przez &c{ADMIN}");

                }

                @Comment("")
                public Tag tag = new Tag();

                public static class Tag extends OkaeriConfig {

                    public SendableMessage noValueGiven = ChatHolder.message("&cPodaj nowy tag!");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {OLD_TAG}")
                    public SendableMessage changed = ChatHolder.message("&7Zmieniłeś tag gildii &c{TAG} &7na &c{VALUE}&7!");

                }

                @Comment("")
                public Name name = new Name();

                public static class Name extends OkaeriConfig {

                    public SendableMessage noValueGiven = ChatHolder.message("&cPodaj nową nazwę!");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {OLD_NAME}")
                    public SendableMessage changed = ChatHolder.message("&7Zmieniłeś nazwę gildii &c{TAG} &7na &c{VALUE}&7!");

                }

                @Comment("")
                public Validity validity = new Validity();

                public static class Validity extends OkaeriConfig {

                    public SendableMessage noValueGiven = ChatHolder.message("&cPodaj czas o jaki ma byc przedłużona ważność gildii!");
                    public SendableMessage banned = ChatHolder.message("&cTa gildia jest zbanowana!");

                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {DATE}")
                    public SendableMessage changed = ChatHolder.message("&7Przedłużyłeś ważność gildii &c{TAG} &7do &c{DATE}&7!");

                }

                @Comment("")
                public Protection protection = new Protection();

                public static class Protection extends OkaeriConfig {

                    public SendableMessage noValueGiven = ChatHolder.message("&cPodaj date ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {DATE}")
                    public SendableMessage changed = ChatHolder.message("&7Ustawiłeś ochronę gildii &c{TAG} &7do &c{DATE}&7!");

                }

                @Comment("")
                public Lives lives = new Lives();

                public static class Lives extends OkaeriConfig {

                    public SendableMessage noValueGiven = ChatHolder.message("&cPodaj liczbę żyć!");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {VALUE")
                    public SendableMessage changed = ChatHolder.message("&7Zmieniłeś liczbę żyć gildii &c{TAG} &7na &c{VALUE}&7!");

                }

                @Comment("")
                public Move move = new Move();

                public static class Move extends OkaeriConfig {

                    @Comment("Dostępne zmienne: {TAG}, {NAME}")
                    public SendableMessage moved = ChatHolder.message("&7Przeniosłeś teren gildii &c{TAG}&7!");

                }

                @Comment("")
                public Teleport teleport = new Teleport();

                public static class Teleport extends OkaeriConfig {

                    @Comment("Dostępne zmienne: {TAG}, {NAME}")
                    public SendableMessage teleported = ChatHolder.message("&7Teleportowałeś się na teren gildii &c{TAG}&7!");

                }

                @Comment("")
                public Base base = new Base();

                public static class Base extends OkaeriConfig {

                    public SendableMessage noHome = ChatHolder.message("&cGildia gracza nie ma ustawionej bazy!");

                    @Comment("Dostępne zmienne: {PLAYER}")
                    public SendableMessage teleported = ChatHolder.message("&7Gracz &c{PLAYER} &8został teleportowany do bazy gildii!");
                    @Comment("Dostępne zmienne: {ADMIN}")
                    public SendableMessage teleportedTarget = ChatHolder.message("&7Admin &c{ADMIN} &7teleportował Cię do bazy gildii!");

                }

                @Comment("")
                public Leader leader = new Leader();

                public static class Leader extends OkaeriConfig {

                    public SendableMessage alreadyLeader = ChatHolder.message("&cTen gracz jest juz liderem gildii!");

                }

                @Comment("")
                public Ban ban = new Ban();

                public static class Ban extends OkaeriConfig {

                    public SendableMessage noTimeGiven = ChatHolder.message("&cPodaj czas na jaki ma byc zbanowana gildia!");
                    public SendableMessage noReasonGiven = ChatHolder.message("&cPodaj powod!");
                    public SendableMessage alreadyBanned = ChatHolder.message("&cTa gildia jest juz zbanowana!");

                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {TIME}, {REASON}")
                    public SendableMessage banned = ChatHolder.message("&7Zbanowałeś gildie &c{TAG} &7na okres &c{TIME}&7! Powód: &c{REASON}");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {TIME}, {REASON}, {ADMIN}")
                    public SendableMessage bannedBroadcast = ChatHolder.message("&7Gildia &c{TAG} &7została zbanowana przez &c{ADMIN}&7! Powód: &c{REASON}");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {TIME}, {REASON}, {ADMIN}, {NEWLINE}")
                    public String bannedKick = "&7Zostałeś zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}";

                }

                @Comment("")
                public Unban unban = new Unban();

                public static class Unban extends OkaeriConfig {

                    public SendableMessage notBanned = ChatHolder.message("&cTa gildia nie jest zbanowana!");

                    @Comment("Dostępne zmienne: {TAG}, {NAME}")
                    public SendableMessage unbanned = ChatHolder.message("&7Odbanowałeś gildie &c{GUILD}&7!");
                    @Comment("Dostępne zmienne: {TAG}, {NAME}, {ADMIN}")
                    public SendableMessage unbannedBroadcast = ChatHolder.message("&7Gildia &c{TAG} &7została odbanowana&7!");

                }

                @Comment("")
                public Spy spy = new Spy();

                public static class Spy extends OkaeriConfig {

                    public SendableMessage start = ChatHolder.message("&aOd teraz szpiegujesz graczy!");
                    public SendableMessage stop = ChatHolder.message("&cJuz nie szpiegujesz graczy!");

                }

            }

        }

        @Comment("")
        public SecuritySystem securitySystem = new SecuritySystem();

        public static class SecuritySystem extends OkaeriConfig {

            @Comment("Dostępne zmienne: {PLAYER}, {CHEAT}, {NOTE}")
            public SendableMessage info = ChatHolder.message(
                    "&8[&4Security&8] &7Gracz &c{PLAYER}&7 może używać &c{CHEAT}&7 lub innego cheata o podobnym dzialaniu!",
                    "&8[&4Security&8] &7Notatka: &7{NOTE}"
            );

            @Comment("Dostępne zmienne: {DISTANCE}")
            public String reach = "&7Zaatakowal krysztal z odleglosci &c{DISTANCE} &7kratek!";
            @Comment("Dostępne zmienne: {BLOCKS}")
            public String freeCam = "&7Zaatakowal krysztal przez bloki: &c{BLOCKS}";

        }

    }

    @Comment("")
    @Comment("<------- System Messages -------> #")
    public System system = new System();

    public static class System extends OkaeriConfig {

        public String loginNickTooShort = "&cNick jest za krotki!";
        public String loginNickTooLong = "&cNick jest za długi!";
        public String loginNickInvalid = "&cNick zawiera niedozwolone znaki!";

        @Comment("")
        public SendableMessage commandHelp = ChatHolder.message(
                "&aFunnyGuilds Help:",
                "&b/funnyguilds (reload|rl) &7- przeładuj plugin",
                "&b/funnyguilds (update|check) &7- sprawdź dostępność aktualizacji",
                "&b/funnyguilds save-all &7- zapisz wszystko"
        );

        public SendableMessage reloadWarn = ChatHolder.message("&cDziałanie pluginu FunnyGuilds po reloadzie może byc zaburzone, zalecane jest przeprowadzenie restartu serwera!");
        public SendableMessage reloadTime = ChatHolder.message("&aFunnyGuilds &7przeładowano! (&b{TIME}s&7)");
        public SendableMessage reloadReloading = ChatHolder.message("&7Przeładowywanie...");
        public SendableMessage saveAllSaving = ChatHolder.message("&7Zapisywanie...");
        public SendableMessage saveAllSaved = ChatHolder.message("&7Zapisano (&b{TIME}s&7)!");
        public SendableMessage pluginVersion = ChatHolder.message("&7FunnyGuilds &b{VERSION} &7by &bFunnyGuilds Team");
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

    }

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        try {
            this.colorFields(this);
        } catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not load message configuration", ex);
        }

        return this;
    }

    private void colorFields(OkaeriConfig section) throws IllegalAccessException {
        for (Field field : section.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();
            if (type.equals(String.class)) {
                field.set(this, ChatUtils.colored((String) field.get(this)));
            } else if (type.equals(List.class)) {
                List<String> list = (List<String>) field.get(this);
                list.replaceAll(ChatUtils::colored);
            } else if (type.equals(OkaeriConfig.class)) {
                this.colorFields((OkaeriConfig) field.get(this));
            }
        }
    }

}
