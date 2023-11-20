package net.dzikoysk.funnyguilds.user.config;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.time.Duration;
import net.dzikoysk.funnyguilds.config.AutoColor;
import net.dzikoysk.funnyguilds.config.ConfigSection;

public class ScoreboardConfiguration extends ConfigSection {

    @Comment("Czy systemy oparte na scoreboardzie (podane niżej) powinny być włączone?")
    public boolean enabled = true;

    @Comment("")
    @Comment("Czy FunnyGuilds powinno korzystać ze współdzielonego scoreboarda")
    @Comment("Ta opcja pozwala na wspólne działanie pluginu FunnyGuilds oraz innych pluginów modyfikujących scoreboard")
    @Comment("UWAGA: opcja eksperymentalna i może powodować błędy przy wyświetlaniu rzeczy zależnych od scoreboardów!")
    public boolean useSharedScoreboard = false;

    @Comment("")
    public NameTag nametag = new NameTag();

    public static class NameTag extends ConfigSection {

        @Comment("Czy system nametagów powinien być włączony (wartość przed i po nicku gracza)")
        public boolean enabled = true;

        @Comment("")
        @Comment("Co jaki czas nametagi wszystkich graczy powinny być odświeżane (niezależnie od innych triggerów)")
        @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
        @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
        @Comment("Przykład: 1m30s")
        public Duration updateRate = Duration.ofMinutes(1);

        @Comment("")
        @Comment("Konfiguracja prefixu (wartość przed nickiem gracza)")
        @Comment("Wspierane placeholdery: {TAG}, {REL_TAG}, {NAME}, {POS}")
        @Comment("Wspierane jest również PlaceholderAPI (w tym relacyjne placeholdery)")
        public Value prefix = new Value("", "{REL_TAG} ");

        @Comment("")
        @Comment("Konfiguracja suffixu (wartość po nicku gracza)")
        @Comment("Wspierane placeholdery: {TAG}, {REL_TAG}, {NAME}, {POS}")
        @Comment("Wspierane jest również PlaceholderAPI (w tym relacyjne placeholdery)")
        public Value suffix = new Value();

        public static class Value extends ConfigSection {

            @AutoColor
            private String noGuild = "";
            @AutoColor
            private String ourGuild = "";
            @AutoColor
            private String alliesGuild = "";
            @AutoColor
            private String enemiesGuild = "";
            @AutoColor
            private String otherGuild = "";

            public Value(String noGuild, String ourGuild, String alliesGuild, String enemiesGuild, String otherGuild) {
                this.noGuild = noGuild;
                this.ourGuild = ourGuild;
                this.alliesGuild = alliesGuild;
                this.enemiesGuild = enemiesGuild;
                this.otherGuild = otherGuild;
            }

            public Value(String noGuild, String anyGuild) {
                this(noGuild, anyGuild, anyGuild, anyGuild, anyGuild);
            }

            public Value() {
            }

            public String getNoGuild() {
                return this.noGuild;
            }

            public String getOurGuild() {
                return this.ourGuild;
            }

            public String getAlliesGuild() {
                return this.alliesGuild;
            }

            public String getEnemiesGuild() {
                return this.enemiesGuild;
            }

            public String getOtherGuild() {
                return this.otherGuild;
            }

        }

    }

    @Comment("")
    public Dummy dummy = new Dummy();

    public static class Dummy extends ConfigSection {

        @Comment("Czy włączyć dummy z punktami (liczbę punktów pod nickiem gracza)")
        @Comment("UWAGA: zalecane jest wyłączenie tej opcji w przypadku konfliktów z BungeeCordem, więcej szczegółów tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
        public boolean enabled = true;

        @Comment("")
        @Comment("Co jaki czas dummy wszystkich graczy powinny być odświeżane (niezależnie od innych triggerów)")
        @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
        @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
        @Comment("Przykład: 1m30s")
        public Duration updateRate = Duration.ofMinutes(1);

        @Comment("")
        @Comment("Wygląd nazwy wyświetlanej za punktami")
        @AutoColor
        public String suffix = "pkt";

    }

    @Comment("")
    public QueueConfiguration queueConfiguration = new QueueConfiguration();

    public static class QueueConfiguration extends ConfigSection {

        @Comment("Co jaki czas kolejka (nametagów/dummy) powinna być odświeżana")
        @Comment("Wartość podawana w tickach (1 sekunda = 20 ticków)")
        @Comment("Wyższe wartości mogą powodować opóźnienia w aktualizacji nametagów/dummy, ale zmniejszają ryzyko lagów")
        public int updateRate = 2;

        @Comment("")
        @Comment("Maksymalna liczba update'ów w trakcie trwania jednego ticku")
        @Comment("Wyższe wartości zmniejszają ryzyko opóźnień podczas aktualizacji nametagów/dummy, ale mogą powodować lagi")
        public int maxUpdatesInTick = 100;


    }

}
