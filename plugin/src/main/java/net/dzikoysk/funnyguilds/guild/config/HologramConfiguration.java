package net.dzikoysk.funnyguilds.guild.config;

import com.google.common.collect.ImmutableList;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.List;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class HologramConfiguration extends ConfigSection {

    @Comment("Czy hologram nad sercem gildii ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Co ile ticków ma być aktualizowany hologram (20 ticków = 1 sekunda)")
    public long updateInterval = 400L;

    @Comment("")
    @Comment("Przedmiot ozdobny nad hologramem")
    @Comment("Jeśli ustawiony na AIR - opcja jest wyłączona")
    public Material item = Material.AIR;

    @Comment("")
    @Comment("Tekst wyświetlany przez hologram")
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {KDA}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
    public List<String> displayedLines = new ImmutableList.Builder<String>()
            .add("&7&m-------------------")
            .add("&7- &bFunnyGuilds &7-")
            .add("&7- &bgithub.com/funnyguilds/ &7-")
            .add("&7- &bGildia {TAG} &7-")
            .add("&7- &bZycia {LIVES} &7-")
            .add("&7~ Created by &bFunnyGuilds Team &7~")
            .add("&7&m-------------------")
            .build();

    @Comment("")
    @Comment("Korekcja lokalizacji hologramu względem startowej lokalizacji")
    public Vector locationCorrection = new Vector(0.5, 2.5, 0.5);

}
