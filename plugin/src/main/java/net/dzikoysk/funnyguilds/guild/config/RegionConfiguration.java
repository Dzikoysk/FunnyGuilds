package net.dzikoysk.funnyguilds.guild.config;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.validator.annotation.Positive;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import net.dzikoysk.funnyguilds.config.requirement.RequirementsComponent;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerStorage;
import org.bukkit.Material;

public class RegionConfiguration extends ConfigSection {

    @Comment("Czy tworzenie regionów gildii, oraz inne związane z nimi rzeczy, mają byc włączone")
    @Comment("UWAGA - dobrze przemyśl decyzję o wyłączeniu regionów!")
    @Comment("Gildie nie będą miały w sobie żadnych informacji o regionach, a jeśli regiony są włączone - te informacje muszą byc obecne")
    @Comment("Jeśli regiony miałyby być znowu włączone - będzie trzeba wykasować WSZYSTKIE dane pluginu")
    @Comment("Wyłączenie tej opcji nie powinno spowodować żadnych błędów, jeśli już są utworzone regiony gildii")
    public boolean enabled = true;

    @Comment
    public HeartConfiguration heart = new HeartConfiguration();

    @Comment
    public Enlarge enlarge = new Enlarge();

    public static class Enlarge extends ConfigSection {

        @Positive
        @Comment("Domyślna wielkość regionu gildii (podana w liczbie bloków od środka gidii)")
        public int defaultSize = 50;

        @Positive
        @Comment
        @Comment("O ile ma być powiększany region gildii po każdym jej powiększeniu (podana w liczbie bloków od środka gidii)")
        public int enlargeSize = 5;

        @Comment
        @Comment("Konfiguracja wymagań do powiększenia regionu gildii")
        @Comment("Lista powinna być poukładana przedmiotami dla najmniejszego powiększenia, do największego")
        @Comment("Liczba elementów w liście jest równoznaczna z liczbą poziomów powiększenia regionu")
        @Comment("Format konfiguracji:")
        @Comment("requirements: (https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-%E2%80%90-Konfigurajca#requirements)")
        @Comment("- <wymagania 1> # poziom 1")
        @Comment("- <wymagania 2> # poziom 2")
        @Comment("- ...")
        @Comment("- <wymagania n> # poziom n")
        public List<RequirementsComponent> requirements = List.of();

    }

    @Comment
    public Protection protection = new Protection();

    public static class Protection extends ConfigSection {

        @Comment("Bloki z którymi nie można wchodzić w interakcje na terenie obcej gildii")
        public Set<Material> blockedInteractions = Set.of(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER);

        @Comment
        @Comment("Komendy których użycie jest zablokowane na terenie obcej gildii")
        public Set<String> blockedCommands = Set.of("sethome");

        @Comment
        @Comment("Bloki, które można stawiać na terenie gildii, niezależnie od tego, czy jest się jej członkiem")
        @Comment("Zostaw puste, aby wyłączyć")
        @Comment("Nazwy bloków muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
        public Set<Material> placingBlocksBypass = Collections.emptySet();

        @Comment
        public Teleport teleport = new Teleport();

        public static class Teleport extends ConfigSection {

            @Comment("Czy ma być blokowana teleportacja na teren neutralnej gildii")
            public boolean neutral = true;

            @Comment
            @Comment("Czy ma być blokowana teleportacja na teren wrogiej gildii")
            public boolean enemy = true;

            @Comment
            @Comment("Czy ma być blokowana teleportacja na teren sojuszniczej gildii")
            public boolean ally = false;

            @Override
            public void processProperties() {
                if (this.neutral || this.enemy || this.ally) {
                    DynamicListenerStorage.teleportEvent = true;
                }
            }

        }

    }

}
