package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.dzikoysk.funnyguilds.config.NumberFormatting;
import net.dzikoysk.funnyguilds.config.RawString;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TopConfiguration extends OkaeriConfig {

    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts")
    public Set<String> enabledUserTops = new TreeSet<>(Arrays.asList("points", "kills", "deaths"));

    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts, avg_points, avg_kills, avg_deaths, avg_assists, avg_logouts")
    public Set<String> enabledGuildTops = new TreeSet<>(Arrays.asList("kills", "deaths", "avg_points"));

    @Comment("Czy placeholdery {PTOP-x} oraz {GTOP-x} maja byc wlaczone. Wylaczenie tej funkcji moze minimalnie zwiekszyc wydajnosc.")
    public boolean enableLegacyPlaceholders = true;

    public Format format = new Format();

    public static class Format extends OkaeriConfig {

        @Comment("Znacznik z wartoscia dodawany do zmiennej {PTOP-typ-x}")
        @Comment("Uzywaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jesli nie chcesz wyswietlac wartosci, tylko sam nick - nie podawaj tu nic")
        public RawString ptop = new RawString(" &7[{VALUE}&7]");

        @Comment("Znacznik z wartoscia dodawany do zmiennej {GTOP-typ-x}")
        @Comment("Uzywaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jesli nie chcesz wyswietlac wartosci, tylko sam tag - nie podawaj tu nic")
        public RawString gtop = new RawString(" &7[&b{VALUE}&7]");

        public List<NumberFormatting> valueFormatting = Arrays.asList(
                new NumberFormatting(0, 749, "&4{VALUE}"),
                new NumberFormatting(750, 999, "&c{VALUE}"),
                new NumberFormatting(1000, 1499, "&a{VALUE}"),
                new NumberFormatting(1500, Float.MAX_VALUE, "&6&l{VALUE}")
        );

    }

}
