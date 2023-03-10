package net.dzikoysk.funnyguilds.config.sections;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TopConfiguration extends OkaeriConfig {

    @Comment("Lista topek graczy, które mają być obliczane i parsowane")
    @Comment("Najlepiej włączyć tylko te, które będą używane, żeby niepotrzebnie nie obciążać serwera")
    @Comment("Topek tych można używać w formie placeholderów {PTOP-typ-x}, np. na tabie czy w PlaceholderAPI")
    @Comment("Dostępne typy topek: points, kills, deaths, assists, logouts")
    public Set<String> enabledUserTops = new TreeSet<>(Arrays.asList("points", "kills", "deaths"));

    @Comment("")
    @Comment("Lista topek gildii, które mają być obliczane i parsowane")
    @Comment("Najlepiej włączyć tylko te, które będą używane, żeby niepotrzebnie nie obciążać serwera")
    @Comment("Topek tych można używać w formie placeholderów {GTOP-typ-x}, np. na tabie czy w PlaceholderAPI")
    @Comment("Dostępne typy topek: points, kills, deaths, assists, logouts, avg_points, avg_kills, avg_deaths, avg_assists, avg_logouts")
    public Set<String> enabledGuildTops = new TreeSet<>(Arrays.asList("kills", "deaths", "avg_points"));

    @Comment("")
    public Format format = new Format();

    public static class Format extends OkaeriConfig {

        @Comment("Znacznik z wartością dodawany do zmiennej {PTOP-typ-x}")
        @Comment("Używaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jeśli nie chcesz wyświetlać wartości, tylko sam nick - nie podawaj tu nic")
        public RawString ptop = new RawString(" &7[{VALUE}&7]");

        @Comment("")
        @Comment("Znacznik z wartością dodawany do zmiennej {GTOP-typ-x}")
        @Comment("Używaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jeśli nie chcesz wyświetlać wartości, tylko sam tag - nie podawaj tu nic")
        public RawString gtop = new RawString(" &7[&b{VALUE}&7]");

        @Comment("")
        @Comment("Wygląd znacznika {VALUE-FORMAT} w zależności od wartości topki graczy")
        @Comment("Lista powinna być podana od najmniejszych do największych rankingów")
        @Comment("Elementy listy powinny być postaci: \"minValue-maxValue wygląd\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
        @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w górę, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma być używane dane formatowanie")
        public Map<String, List<RangeFormatting>> ptopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

        @Comment("")
        @Comment("Wygląd znacznika {VALUE-FORMAT} w zależności od wartości topki gildii")
        @Comment("Lista powinna być podana od najmniejszych do największych rankingów")
        @Comment("Elementy listy powinny być postaci: \"minValue-maxValue wygląd\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
        @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w górę, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma być używane dane formatowanie")
        public Map<String, List<RangeFormatting>> gtopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("avg_points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

    }

    @Comment("")
    @Comment("Czy zmienne typu {PTOP-x} oraz {GTOP-x} powinny być pokolorowane w zależności od relacji gildyjnych")
    public boolean useRelationshipColors = false;

}
