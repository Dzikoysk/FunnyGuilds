package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import net.dzikoysk.funnyguilds.config.FunnyTime;

public class TntProtectionConfiguration extends ConfigSection {

    public TimeConfig time = new TimeConfig();

    public static class TimeConfig extends ConfigSection {

        @Comment("Czy włączyć ochronę przed TNT na terenach gildii w podanych godzinach")
        public boolean enabled = false;

        @Comment("")
        @Comment("Czy włączyć ochronę przed TNT na całym serwerze w podanych godzinach")
        public boolean enabledGlobal = false;

        @Comment("")
        @Comment("O której godzinie ma sie zacząć ochrona przed TNT")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("start-time")
        public FunnyTime startTime = new FunnyTime(22, 0);

        @Comment("")
        @Comment("Do której godziny ma działać ochrona przed TNT")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("end-time")
        public FunnyTime endTime = new FunnyTime(6, 0);

        @Exclude
        public boolean passingMidnight;

    }

    public BuildConfig build = new BuildConfig();

    public static class BuildConfig extends ConfigSection {

        @Comment("Minimalna wysokość, od której można stawiać TNT")
        public int minHeight = 0;

        @Comment("")
        @Comment("Maksymalna wysokość, do której można stawiać TNT")
        public int maxHeight = 255;

    }

    public ExplodeConfig explode = new ExplodeConfig();

    public static class ExplodeConfig extends ConfigSection {

        @Comment("Minimalna wysokość, od której TNT wybucha")
        public int minHeight = 0;

        @Comment("")
        @Comment("Maksymalna wysokość, do której TNT wybucha")
        public int maxHeight = 255;

    }

}
