package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class HeartConfiguration extends OkaeriConfig {

    @Comment("Blok lub entity, które jest sercem gildii")
    @Comment("Zmiana entity wymaga pełnego restartu serwera")
    @Comment("Nazwy bloków muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
    @Comment("Typ entity musi byc zgodny z tą lista (i zdrowym rozsądkiem) - https://spigotdocs.okaeri.cloud/select/org/bukkit/entity/EntityType.html")
    @Comment("UWAGA: Zmiana bloku, gdy są juz zrobione jakieś gildie, spowoduje nieprawidłowe działanie ich regionów")
    @Comment(" ")
    @Comment("UWAGA: Jeśli jako serca gildii chcesz użyć bloku, który spada pod wpływem grawitacji - upewnij się, ze będzie on stał na jakimś innym bloku!")
    @Comment("Jeśli pojawi sie w powietrzu - spadnie i plugin nie będzie odczytywał go poprawnie!")
    public String createType = "ender_crystal";
    @Exclude
    public Material createMaterial;
    @Exclude
    public EntityType createEntityType;

    @Comment("")
    @Comment("Konfiguracja domyślnej lokalizacji serca gildii przy jej zakładaniu")
    public Center center = new Center();

    public static class Center extends OkaeriConfig {

        @Comment("Konfiguracja na jakiejś wysokości (wartość y) powinno znajdować się serce gildii")
        @Comment("Dostępne wartości:")
        @Comment(" FIXED - serce gildii będzie znajdowało się na stałej wysokości (patrz opcje 'fixed-height')")
        @Comment(" PLAYER - serce gildii będzie znajdowało się na wysokości gracza")
        @Comment(" GRAVITY - serce gildii będzie znajdywało się na powierzchni terenu")
        public Height height = Height.FIXED;

        public enum Height {

            FIXED,
            PLAYER,
            GRAVITY

        }

        @Comment("")
        @Comment("Na jakim poziomie ma być wyznaczone centrum gildii (jeśli ustawienie 'height' to 'FIXED')")
        public int fixedHeight = 60;

        @Comment("")
        @Comment("Ostateczne przesunięcie centrum gildii")
        @Comment("Przydatne m.in. kiedy użyjemy opcji 'GRAVITY' i chcemy żeby serce było np. 10 kratek pod powierzchnią")
        public Vector offset = new Vector(0, 0, 0);

        public void prepareCenterLocation(Location location) {
            World world = location.getWorld();
            if (this.height == Height.FIXED) {
                location.setY(this.fixedHeight);
            }
            else if (this.height == Height.GRAVITY) {
                location.setY(world.getHighestBlockYAt(location));
            }
            location.add(this.offset);

            int minHeight = LocationUtils.getMinHeight(world);
            if (location.getBlockY() < minHeight) {
                location.setY(minHeight + 2);
            }
        }

    }

    @Comment("")
    @Comment("Konfiguracja hologramu nad sercem gildii")
    public HologramConfiguration hologram = new HologramConfiguration();

    @Comment("")
    @Comment("Czy ma się tworzyć kula z obsydianu dookoła centrum gildii")
    public boolean createCenterSphere = true;

    @Comment("")
    @Comment("Czy przy tworzeniu gildii powinien być wklejany schemat")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicOnCreation = false;

    @Comment("")
    @Comment("Nazwa pliku ze schematem początkowym gildii")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @Comment("Schemat musi znajdować się w folderze FunnyGuilds")
    public String guildSchematicFileName = "funnyguilds.schematic";

    @Comment("")
    @Comment("Czy schemat przy tworzeniu gildii powinien być wklejany razem z powietrzem")
    @Comment("Przy dużych schematach ma to wpływ na wydajność")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    public boolean pasteSchematicWithAir = true;

    @Exclude
    public File guildSchematicFile;

    @Comment("")
    @Comment("Czy gracz powinien być teleportowany do serca gildii przy jej zakładaniu.")
    public boolean teleportToHeartOnCreate = true;

    @Comment("")
    @Comment("Przesunięcie domyślnego home gildii względem serca gildii")
    @Comment("Opcja przydatna w przypadku ustawienie własnego schematu serca, aby gracze nie pojawiali sie w bloku")
    public Vector homeOffset = new Vector(0, 0, 0);

    @Comment("")
    @Comment("Pozycja głowy gracza dla domyślnego home gildii")
    public HeadPosition homeHeadPosition = new HeadPosition(0, 0);

    public static class HeadPosition extends OkaeriConfig {

        private float yaw;
        private float pitch;

        public HeadPosition(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getYaw() {
            return this.yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void setHeadPosition(Location location) {
            location.setYaw(this.yaw);
            location.setPitch(this.pitch);
        }

    }

    public InteractionProtection interactionProtection = new InteractionProtection();

    public static class InteractionProtection extends OkaeriConfig {

        @Comment("Czy blokada interakcji w okolicach serca gildii powinna być włączona")
        public boolean enabled = false;

        @Comment("")
        @Comment("Przesunięcie pierwszego wierzchołka względem serca gildii")
        public Vector firstCorner = new Vector(2, -1, -2);

        @Comment("")
        @Comment("Przesunięcie drugiego wierzchołka względem serca gildii")
        public Vector secondCorner = new Vector(-2, 3, 2);

    }

    public void loadProcessedProperties() {
        this.createEntityType = EntityUtils.parseEntityType(this.createType, true, false);
        if (this.createEntityType == null) {
            this.createMaterial = MaterialUtils.parseMaterial(this.createType, true);
        }

        if (this.pasteSchematicOnCreation) {
            if (FunnyStringUtils.isEmpty(this.guildSchematicFileName)) {
                FunnyGuilds.getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field " +
                        "\"paste-schematic-on-creation\" is set to true!");
                this.pasteSchematicOnCreation = false;
            }
            else {
                this.guildSchematicFile = new File(FunnyGuilds.getInstance().getDataFolder(), this.guildSchematicFileName);

                if (!this.guildSchematicFile.exists()) {
                    FunnyGuilds.getPluginLogger().error("File with given name in field \"guild-schematic-file-name\" does not exist!");
                    this.pasteSchematicOnCreation = false;
                }
            }
        }
    }

}
