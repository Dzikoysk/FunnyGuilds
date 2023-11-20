package net.dzikoysk.funnyguilds.config.tablist;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.Min;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;

public class TablistConfiguration extends ConfigSection {

    @Comment("Czy lista graczy ma być włączona")
    public boolean enabled = true;

    @Comment("")
    @Comment("Wygląd listy graczy, przedział slotów - od 1 do 80")
    @Comment("Schemat wyglądu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/assets/tab-scheme.png")
    @Comment(" ")
    @Comment("> Spis zmiennych gracza:")
    @Comment("{PLAYER} - nazwa gracza")
    @Comment("{WORLD} - świat, w którym znajduje się gracz")
    @Comment("{PING} - ping gracza")
    @Comment("{PING-FORMAT} - ping gracza, z formatowaniem")
    @Comment("{GUILD-POSITION} - pozycja gracza w gildii (formatowana jak chat-position-x)")
    @Comment("{POINTS} - punkty gracza")
    @Comment("{POINTS-FORMAT} - punkty gracza, z formatowaniem")
    @Comment("{POSITION-<typ>} - pozycja gracza w rankingu, dla danego typu topki")
    @Comment("{KILLS} - liczba zabójstw gracza")
    @Comment("{DEATHS} - liczba śmierci gracza")
    @Comment("{ASSISTS} - liczba asyst gracza")
    @Comment("{LOGOUTS} - liczba wylogowań gracza podczas walki")
    @Comment("{KDR} - stosunek zabójstw do śmierci gracza")
    @Comment("{KDA} - stosunek zabójstw i asyst do śmierci gracza")
    @Comment("{WG-REGION} - region WorldGuarda, na którym znajduje sie gracz (pierwszy z listy, jeśli jest ich kilka)")
    @Comment("{WG-REGIONS} - regiony WorldGuarda, na których znajduje się gracz (oddzielone przecinkami)")
    @Comment("{VAULT-MONEY} - balans konta gracza, pobierany z pluginu Vault")
    @Comment(" ")
    @Comment("> Spis zmiennych gildyjnych:")
    @Comment("{G-NAME} - nazwa gildii do której należy gracz")
    @Comment("{G-TAG} - tag gildii gracza")
    @Comment("{G-OWNER} - właściciel gildii")
    @Comment("{G-DEPUTIES} - zastępcy gildii")
    @Comment("{G-DEPUTY} - losowy z zastępców gildii")
    @Comment("{G-LIVES} - liczba żyć gildii")
    @Comment("{G-LIVES-SYMBOL} - liczba żyć gildii, w postaci powtarzającego się symbolu (do ustawienia w lives-repeating-symbol.full, w config.yml), z pustymi serduszkami jeśli żyć brakuje (do ustawienia w lives-repeating-symbol.empty, w config.yml)")
    @Comment("{G-LIVES-SYMBOL-ALL} - liczba wszystkich żyć gildii, w postaci powtarzającego się symbolu (do ustawienia w lives-repeating-symbol.full, w config.yml)")
    @Comment("{G-ALLIES-ALL} - liczba sojuszników gildii")
    @Comment("{G-ENEMIES-ALL} - liczba przeciwników gildii")
    @Comment("{G-POINTS} - suma punktów członków gildii")
    @Comment("{G-AVG-POINTS} - średnia liczba punktów członków gildii")
    @Comment("{G-AVG-POINTS-FORMAT} - średnia liczba punktów członków gildii, z formatowaniem")
    @Comment("{G-POSITION-<typ>} - pozycja gildii gracza w rankingu, dla danego typu topki")
    @Comment("{G-KILLS} - suma zabójstw członków gildii")
    @Comment("{G-AVG-KILLS} - średnia liczba zabójstw członków gildii")
    @Comment("{G-DEATHS} - suma śmierci członków gildii")
    @Comment("{G-AVG-DEATHS} - średnia liczba śmierci członków gildii")
    @Comment("{G-ASSISTS} - suma asyst członków gildii")
    @Comment("{G-AVG-ASSISTS} - średnia liczba asyst członków gildii")
    @Comment("{G-LOGOUTS} - suma wylogowań członków gildii")
    @Comment("{G-AVG-LOGOUTS} - średnia liczba wylogowań członków gildii")
    @Comment("{G-KDR} - stosunek zabójstw do śmierci gildii")
    @Comment("{G-KDA} - stosunek zabójstw i asyst do śmierci gildii")
    @Comment("{G-AVG-KDR} - średni stosunek zabójstw do śmierci członków gildii")
    @Comment("{G-AVG-KDA} - średni stosunek zabójstw i asyst do śmierci członków gildii")
    @Comment("{G-MEMBERS-ONLINE} - liczba członków gildii online")
    @Comment("{G-MEMBERS-ALL} - liczba wszystkich członków gildii")
    @Comment("{G-VALIDITY} - data wygaśnięcia gildii")
    @Comment("{G-VALIDITY-TIME} - czas do wygaśnięcia gildii (np. 5 dni 2 godziny 1 minuta)")
    @Comment("{G-PROTECTION} - data wygaśnięcia ochrony gildii")
    @Comment("{G-PROTECTION-TIME} - czas do wygaśnięcia ochrony gildii (np. 5 dni 2 godziny 1 minuta)")
    @Comment("{G-REGION-SIZE} - rozmiar gildii")
    @Comment(" ")
    @Comment("> Spis pozostałych zmiennych:")
    @Comment("{GUILDS} - liczba gildii na serwerze")
    @Comment("{USERS} - liczba użytkowników serwera")
    @Comment("{ONLINE} - liczba graczy online")
    @Comment("{TPS} - TPS serwera")
    @Comment("{SECOND} - sekunda")
    @Comment("{MINUTE} - minuta")
    @Comment("{HOUR} - godzina")
    @Comment("{DAY_OF_WEEK} - dzień tygodnia, wyrażony w postaci nazwy dnia")
    @Comment("{DAY_OF_MONTH} - dzień miesiąca, wyrażony w postaci liczby")
    @Comment("{MONTH} - miesiąc, wyrażony w postaci nazwy miesiąca")
    @Comment("{MONTH_NUMBER} - miesiąc, wyrażony w postaci liczby")
    @Comment("{YEAR} - rok")
    @Comment("{PTOP-<typ>-<pozycja>} - gracz na podanej pozycji w topce dla danego typu. Lista dostępnych typów znajduje się w 'config.yml' pod kluczem 'top.enabled-user-tops'")
    @Comment("{GTOP-<typ>-<pozycja>} - gildia na podanej pozycji w topce dla danego typu. Lista dostępnych typów znajduje się w 'config.yml' pod kluczem 'top.enabled-guild-tops'")
    public Map<Integer, String> cells = ImmutableMap.<Integer, String>builder()
            .put(2, " &b&lSTATYSTYKI")
            .put(4, " &7Nick: &b{PLAYER}")
            .put(6, " &7Punkty: &b{POINTS}")
            .put(7, " &7Zabójstwa: &b{KILLS}")
            .put(8, " &7Śmierci: &b{DEATHS}")
            .put(9, " &7Asysty: &b{ASSISTS}")
            .put(10, " &7KDR: &b{KDR}")
            .put(12, " &b&lINFORMACJE")
            .put(14, " &7Ping: &b{PING}")
            .put(15, " &7TPS: &b{TPS}")
            .put(17, " &7Online: &b{ONLINE}")
            .put(19, " &7Czas: &b{DAY_OF_MONTH} {MONTH} {YEAR}&7, &b{HOUR}:{MINUTE}:{SECOND}")
            .put(22, " &b&lSTATYSTYKI GILDII")
            .put(24, " &7Gildia: &b{G-TAG}")
            .put(26, " &7Punkty: &b{G-AVG-POINTS}")
            .put(27, " &7Zabójstwa: &b{G-KILLS}")
            .put(28, " &7Śmierci: &b{G-DEATHS}")
            .put(29, " &7Asysty: &b{G-ASSISTS}")
            .put(30, " &7KDR: &b{G-KDR}")
            .put(32, " &7Życia: &b{G-LIVES-SYMBOL} &8(&b{G-LIVES}&8)")
            .put(42, " &b&lTOP &8- &b&lGracze")
            .put(44, " &7Gracze: &b{USERS}")
            .put(46, " &b1. &7{PTOP-POINTS-1}")
            .put(47, " &b2. &7{PTOP-POINTS-2}")
            .put(48, " &b3. &7{PTOP-POINTS-3}")
            .put(49, " &b4. &7{PTOP-POINTS-4}")
            .put(50, " &b5. &7{PTOP-POINTS-5}")
            .put(51, " &b6. &7{PTOP-POINTS-6}")
            .put(52, " &b7. &7{PTOP-POINTS-7}")
            .put(53, " &b8. &7{PTOP-POINTS-8}")
            .put(54, " &b9. &7{PTOP-POINTS-9}")
            .put(55, " &b10. &7{PTOP-POINTS-10}")
            .put(57, " &7Twoja pozycja: &b{POSITION-POINTS}")
            .put(62, " &b&lTOP &8- &b&lGildie")
            .put(64, " &7Gildie: &b{GUILDS}")
            .put(66, " &b1. &7{GTOP-AVG_POINTS-1}")
            .put(67, " &b2. &7{GTOP-AVG_POINTS-2}")
            .put(68, " &b3. &7{GTOP-AVG_POINTS-3}")
            .put(69, " &b4. &7{GTOP-AVG_POINTS-4}")
            .put(70, " &b5. &7{GTOP-AVG_POINTS-5}")
            .put(71, " &b6. &7{GTOP-AVG_POINTS-6}")
            .put(72, " &b7. &7{GTOP-AVG_POINTS-7}")
            .put(73, " &b8. &7{GTOP-AVG_POINTS-8}")
            .put(74, " &b9. &7{GTOP-AVG_POINTS-9}")
            .put(75, " &b10. &7{GTOP-AVG_POINTS-10}")
            .put(77, " &7Pozycja gildii: &b{G-POSITION-AVG_POINTS}")
            .build();

    @Comment("")
    @Comment("Wygląd nagłówka listy graczy")
    public String header = "&7FunnyGuilds &b4.13.0 Snowdrop &8- &bgithub.com/funnyguilds";

    @Comment("")
    @Comment("Wygląd stopki listy graczy")
    public String footer = "&c&lWiadomości braku (pokazujące się, gdy gracz nie ma gildii) można zmienić w plikach w katalogu &6&llang&c&l!";

    @Comment("")
    @Comment("Wygląd głowek na liście graczy")
    @Comment("Funkcja działa jedynie dla graczy premium, gracze non-premium nie zobaczą żadnych zmian")
    public Heads heads = new Heads();

    public static class Heads extends ConfigSection {

        @Comment("Czy własne główki na liście graczy maja byc włączone")
        public boolean enabled = true;

        @Comment("")
        @Comment("Tekstura głowy, która ma byś wyświetlana na liście graczy")
        @Comment("Teksturę można wziąć z np. https://mineskin.org/ (można wybrać z galerii lub utworzyć własną)")
        public Map<NumberRange, SkinTexture> textures = ImmutableMap.<NumberRange, SkinTexture>builder()
                .put(new NumberRange(1, 80), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0OTkzMjg0OTYyOCwKICAicHJvZmlsZUlkIiA6ICIzMjIzNDVjNzg2ZTY0ZWU3OWNlZDA3NTAzMmI5MzQxZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZXJpZDB0XyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZWUyMTYxODk0NjA5M2QzZDYzNjk1M2M0ZTM4Mzc5NDM3MjFhOWE4N2NiZGRlZmEwYmVmMmZjZmY0NTQ1ZGU4IgogICAgfQogIH0KfQ==",
                        "JK7IOCq36OLWWKmqp0NZHtZ0gKCXHgXWiHJS7d648X+5Nk7JrYhrV0GksyNOlBZzu+q0OtMGOnL/o+c9UFoedfIwEojjCv9Aty8knKU0+xfD190148nkXxEqOw6X/JJIe+7oWAQ+dBm7E/9LID2nU4X0clbWHFaFm5yFEbvuSYHdXrI6WRmAZoaacTOI5AVAudOz2nc3PCl9Zwl/Uzf1YosLOyYc3VQKGEnWSZHqiDWOGsPpHuVmQazOtgeg39LyfCDiDi7dT33nQjdiGab3DTYLIXNzKvG3pijzJGfs5seV4RJNlHWosmmVSfvmEOw6AONWMxxNnKd6aEl2OLIDc7SeZT6zgvyXtd7MZ6WtWB7d+8aBdH7hRodtDnUXIdiqFuQcT/3Z+ljU1N0rTn7e/RQ6Ci+/U05V/BTGJ1/reYldEjhT2M+3A93ydWP6qclzztWxjdOwqOS2ga4Z6KcsnnQCkbR+ZfUbCdrwSEW8bg6iwUb4ZJc5FIqe7is5hT8h4HU5U1ODTzC0qNZ5jwPNv35FVEy33zeZjrYvAYRWPkjr5ro1TIsP8pnrzK+SAsnVjq1+wNltPPF0DYRncMqVPlQ9jH4QwrHP5MHlTTZm3O5d2M8v62RhOx+lUjpIZes6Si0BChMrZOGnmr/M2h5bIkxEpBLnmvaHgVkLBdm+hx0="
                ))
                .put(new NumberRange(46, 46), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU4ODc3MTU5NzE5OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVjN2VlYTBlOTMzMmIzZGEyYjk0MTY5Y2ZmMjI2ZTlmOTAwMTg3MmY2ZjZhN2U5Yjc3OTI2MmRlMDAyYWQzYyIKICAgIH0KICB9Cn0=",
                        "ukTVfQx0saoV8CjINbZUO4sbfebp122ErB2ltCjqiTZUWTUQVVm2k7yBanlqKFhHQQE2fSjgdvNaJ7zRaDDXy4d8ZUKQTeIW4vPtnebyxnwscfJQzZ5qYGJn4RPnM/mEUGf14QS4pz6U7SK0Xb5YpWRNXsm6LfOirwbcwjAH1TEFBnVpkspIySo/5f/6Y1tROXXm3BGsY38MXeG3ItgMRjsJhDmXnJnf0lkIX0EYUMq1F/pf9rsHaCine3vELpL97zUTxjAdgJ/cU3kGzDi2bLfQq7SCOBiMs1Mpi9AvXyQAjG52z4IuX6id958lg3pwHYDO713p5YPaCbuxYoaY2JJJTHtwjigKcnBvFgqNbHE9wIVe+vPd3y63YfXYdSoErDlO32vBpf1VuQhoN5LPfGKUadCU/f6QcE6eXtztIbiWHFQ/ZoceCEvDHu5d76jIuAV18YlYOi7JruYorfyG3WdvNmR0nVzYHJ8wZdny081fkglF3G4dfraa6rJ3eiR62B3YpCAqv4qpRucsYyM9P5Ct8tmhxAGM8Nybu0+QtkNnBalbm8JFY6ATiZ6xHqBmQgypMFqpiarM3euLLR1U+pdoxxGkM9655RSbR/jvV2bcBRrvYj0UllqVUeqp7trI7Ch6ogNMOk6ZxrRstL6/mRA/BVyK3r5FsTzpU1IZIlE="
                ))
                .put(new NumberRange(47, 47), new SkinTexture(
                        "eyJ0aW1lc3RhbXAiOjE1MTgyNTQ4MjQ0OTQsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJiNzYzMTc3YTdkN2UzN2E1YmNkMTkwMzAzYzI0MjQyMWY1Njg3YmRkZGIyMTc0N2EzZjA5MTMzZDUzNjg1In19fQ==",
                        "JRLfdFqhjxEGuFnkfhgyLw7/kGqmJS9a5p7oJDMqG9zeaV4E0lfcU2UXH9j8+3gblHTYYS2lftQ3BS2AJx2vA4A+3Y2Wj0C4t6BRa87RVZIJfEObbDFnCuESwwQYpJ+vyefI224Uto7cOJfqcq9G/TeV/tkZkfuPjJs1xw+ZX4gsVYPzFqhdlejRrmAfZJ5KasRLSi2HVCk7y/Vh6a0mPZm12eRK15RaANoQKqkA8dpSvW/OF/coh9ig0ydGUmCBjGphg5LkgFSQv+iFIeDJTz6CNbZrnb1haDq1HxKWjkJWDKmuBzMug04vkue5bxYNTVKRPazEh0PSdv2RmNLsClRMhFepN1vQUarJTRwEwHLR6mdjPsbb/LYvdVzqlgyO2y/XBcdWbOUSq0486S9vzHHDMvb0hCsKHWkKEyrs5O73+QtJhSU4iKGGJHwf3I3VC9wJlBAFNkZT7IU3B+ZBNJlG3IGqfB3Rda75dZDT5a9NKrwrdgi58lsNkZziu5tUOSFLIsB7aWJnDoyAXAe+AQopOmv+oKvP1EbdKBGAGOY6s4/UgnIytTCiSQSBb63QBDLfB6AoVXCfa48mruAXvBLUGt1BYQ7tXWA8vWdi01TMPx0iCfISW8FQJ/1pb4LHxRPrVJEFGSqt6kH7X+UNj+oX16mehTgSMKi1GPOOvu0="
                ))
                .put(new NumberRange(48, 48), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0ODE3ODEwNzQzOSwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2M3MzdlYTkxZWFkNGNjNTA1NzcxNzQ5NDU3OTMzNzAwNWQ2MjRkMDZhM2QzYzdkNzkyZTZhY2IwYWM3ZiIKICAgIH0KICB9Cn0=",
                        "EdfrzjzISPGfFioxn6X1Jj1eo6jDw6t76PzevgQW7VHg/TJ8BzXL/CH8PTn9zA/MHOmNmhV6FraU9WkOsAwCEGUJC0klhaB97LE/X1qP4Btip3cVv3UaCXQyu20FcQVXoelgNa2vFWUL8aVLUSl0xl2AzTIWo57ktSf/F/oUl7/qQVOTyy3P/mOZl+PiPX3HLYVj7euas+djRC8oNqipRMuqYI+LBG2LvDvlOiupgBAfE68xmbeHQdAj21Sv/nF/CeHHWcYRO2QLKIaBuA4rHma6E9yagAKptOlL8oVGadrlWg1b4xbzjSz78LDX5v/j1Zo1d+xovdYAVjPik9uy4gs1rV7ZWLC0AeN567E4QbXJIkaPrzmAdVWbhs903CfEAQB/RNHhakL9rquGJlkXm5hJjqxLNJoOtjKgH5OHz5oMiCIz4BLFPc0eLNx2jKcNeehAijtvs2o1DRA5pwbPL/2YtHE87Keu6z0VA6RYxWNIntFHILOrD8wBcl3aMuiE3Y59+MTRjeaFgkaNfFugzAUYGl5D3i5IfPLhMpCb66aEifYVvp4MFGEustk9dGNztV4qr1mAufHKlD6iDf4pq+0808w00+GTamaHhOhm++OUT1KZsj/mrsYQwPzkBJ7g8HqyGrrTfZ8Tch6l+GSkUMaVvYngiZ7nQ8ugFzNWyag="
                ))
                .put(new NumberRange(66, 66), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU4ODc3MTU5NzE5OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVjN2VlYTBlOTMzMmIzZGEyYjk0MTY5Y2ZmMjI2ZTlmOTAwMTg3MmY2ZjZhN2U5Yjc3OTI2MmRlMDAyYWQzYyIKICAgIH0KICB9Cn0=",
                        "ukTVfQx0saoV8CjINbZUO4sbfebp122ErB2ltCjqiTZUWTUQVVm2k7yBanlqKFhHQQE2fSjgdvNaJ7zRaDDXy4d8ZUKQTeIW4vPtnebyxnwscfJQzZ5qYGJn4RPnM/mEUGf14QS4pz6U7SK0Xb5YpWRNXsm6LfOirwbcwjAH1TEFBnVpkspIySo/5f/6Y1tROXXm3BGsY38MXeG3ItgMRjsJhDmXnJnf0lkIX0EYUMq1F/pf9rsHaCine3vELpL97zUTxjAdgJ/cU3kGzDi2bLfQq7SCOBiMs1Mpi9AvXyQAjG52z4IuX6id958lg3pwHYDO713p5YPaCbuxYoaY2JJJTHtwjigKcnBvFgqNbHE9wIVe+vPd3y63YfXYdSoErDlO32vBpf1VuQhoN5LPfGKUadCU/f6QcE6eXtztIbiWHFQ/ZoceCEvDHu5d76jIuAV18YlYOi7JruYorfyG3WdvNmR0nVzYHJ8wZdny081fkglF3G4dfraa6rJ3eiR62B3YpCAqv4qpRucsYyM9P5Ct8tmhxAGM8Nybu0+QtkNnBalbm8JFY6ATiZ6xHqBmQgypMFqpiarM3euLLR1U+pdoxxGkM9655RSbR/jvV2bcBRrvYj0UllqVUeqp7trI7Ch6ogNMOk6ZxrRstL6/mRA/BVyK3r5FsTzpU1IZIlE="
                ))
                .put(new NumberRange(67, 67), new SkinTexture(
                        "eyJ0aW1lc3RhbXAiOjE1MTgyNTQ4MjQ0OTQsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJiNzYzMTc3YTdkN2UzN2E1YmNkMTkwMzAzYzI0MjQyMWY1Njg3YmRkZGIyMTc0N2EzZjA5MTMzZDUzNjg1In19fQ==",
                        "JRLfdFqhjxEGuFnkfhgyLw7/kGqmJS9a5p7oJDMqG9zeaV4E0lfcU2UXH9j8+3gblHTYYS2lftQ3BS2AJx2vA4A+3Y2Wj0C4t6BRa87RVZIJfEObbDFnCuESwwQYpJ+vyefI224Uto7cOJfqcq9G/TeV/tkZkfuPjJs1xw+ZX4gsVYPzFqhdlejRrmAfZJ5KasRLSi2HVCk7y/Vh6a0mPZm12eRK15RaANoQKqkA8dpSvW/OF/coh9ig0ydGUmCBjGphg5LkgFSQv+iFIeDJTz6CNbZrnb1haDq1HxKWjkJWDKmuBzMug04vkue5bxYNTVKRPazEh0PSdv2RmNLsClRMhFepN1vQUarJTRwEwHLR6mdjPsbb/LYvdVzqlgyO2y/XBcdWbOUSq0486S9vzHHDMvb0hCsKHWkKEyrs5O73+QtJhSU4iKGGJHwf3I3VC9wJlBAFNkZT7IU3B+ZBNJlG3IGqfB3Rda75dZDT5a9NKrwrdgi58lsNkZziu5tUOSFLIsB7aWJnDoyAXAe+AQopOmv+oKvP1EbdKBGAGOY6s4/UgnIytTCiSQSBb63QBDLfB6AoVXCfa48mruAXvBLUGt1BYQ7tXWA8vWdi01TMPx0iCfISW8FQJ/1pb4LHxRPrVJEFGSqt6kH7X+UNj+oX16mehTgSMKi1GPOOvu0="
                ))
                .put(new NumberRange(68, 68), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0ODE3ODEwNzQzOSwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2M3MzdlYTkxZWFkNGNjNTA1NzcxNzQ5NDU3OTMzNzAwNWQ2MjRkMDZhM2QzYzdkNzkyZTZhY2IwYWM3ZiIKICAgIH0KICB9Cn0=",
                        "EdfrzjzISPGfFioxn6X1Jj1eo6jDw6t76PzevgQW7VHg/TJ8BzXL/CH8PTn9zA/MHOmNmhV6FraU9WkOsAwCEGUJC0klhaB97LE/X1qP4Btip3cVv3UaCXQyu20FcQVXoelgNa2vFWUL8aVLUSl0xl2AzTIWo57ktSf/F/oUl7/qQVOTyy3P/mOZl+PiPX3HLYVj7euas+djRC8oNqipRMuqYI+LBG2LvDvlOiupgBAfE68xmbeHQdAj21Sv/nF/CeHHWcYRO2QLKIaBuA4rHma6E9yagAKptOlL8oVGadrlWg1b4xbzjSz78LDX5v/j1Zo1d+xovdYAVjPik9uy4gs1rV7ZWLC0AeN567E4QbXJIkaPrzmAdVWbhs903CfEAQB/RNHhakL9rquGJlkXm5hJjqxLNJoOtjKgH5OHz5oMiCIz4BLFPc0eLNx2jKcNeehAijtvs2o1DRA5pwbPL/2YtHE87Keu6z0VA6RYxWNIntFHILOrD8wBcl3aMuiE3Y59+MTRjeaFgkaNfFugzAUYGl5D3i5IfPLhMpCb66aEifYVvp4MFGEustk9dGNztV4qr1mAufHKlD6iDf4pq+0808w00+GTamaHhOhm++OUT1KZsj/mrsYQwPzkBJ7g8HqyGrrTfZ8Tch6l+GSkUMaVvYngiZ7nQ8ugFzNWyag="
                ))
                .build();

    }

    @Comment("")
    @Comment("Czy animowana lista graczy ma byc włączona")
    public boolean animated = true;

    @Comment("")
    @Comment("Animowane strony listy graczy")
    @Comment("Wartość 'cycles' to liczba cykli, przez które widać daną stronę na liście graczy")
    @Comment("1 cykl to 1 wysłanie listy, częstotliwość wysyłania listy można ustawić zmieniając wartość 'update-interval'")
    @Comment(" ")
    @Comment("Podsekcję 'cells' konfiguruje się analogicznie jak główną sekcje 'cells' - ustawia się w niej wszystkie komórki, które mają się zmieniać (nadpisując zwykłą konfigurację)")
    @Comment("Podsekcję 'header' konfiguruje się analogicznie jak główną sekcje 'header'")
    @Comment("Podsekcję 'footer' konfiguruje się analogicznie jak główną sekcje 'footer'")
    public List<TablistPage> pages = new LinkedList<>(
            Arrays.asList(
                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                            .put(42, " &b&lTOP &8- &b&lPunkty")
                            .put(46, " &b1. &7{PTOP-POINTS-1}")
                            .put(47, " &b2. &7{PTOP-POINTS-2}")
                            .put(48, " &b3. &7{PTOP-POINTS-3}")
                            .put(49, " &b4. &7{PTOP-POINTS-4}")
                            .put(50, " &b5. &7{PTOP-POINTS-5}")
                            .put(51, " &b6. &7{PTOP-POINTS-6}")
                            .put(52, " &b7. &7{PTOP-POINTS-7}")
                            .put(53, " &b8. &7{PTOP-POINTS-8}")
                            .put(54, " &b9. &7{PTOP-POINTS-9}")
                            .put(55, " &b10. &7{PTOP-POINTS-10}")
                            .put(57, " &7Twoja pozycja: &b{POSITION-POINTS}")
                            .put(62, " &b&lTOP &8- &b&lPunkty")
                            .put(66, " &b1. &7{GTOP-AVG_POINTS-1}")
                            .put(67, " &b2. &7{GTOP-AVG_POINTS-2}")
                            .put(68, " &b3. &7{GTOP-AVG_POINTS-3}")
                            .put(69, " &b4. &7{GTOP-AVG_POINTS-4}")
                            .put(70, " &b5. &7{GTOP-AVG_POINTS-5}")
                            .put(71, " &b6. &7{GTOP-AVG_POINTS-6}")
                            .put(72, " &b7. &7{GTOP-AVG_POINTS-7}")
                            .put(73, " &b8. &7{GTOP-AVG_POINTS-8}")
                            .put(74, " &b9. &7{GTOP-AVG_POINTS-9}")
                            .put(75, " &b10. &7{GTOP-AVG_POINTS-10}")
                            .put(77, " &7Pozycja gildii: &b{G-POSITION-AVG_POINTS}")
                            .build(),
                            null, null),

                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                            .put(42, " &b&lTOP &8- &b&lZabojstwa")
                            .put(46, " &b1. &7{PTOP-KILLS-1}")
                            .put(47, " &b2. &7{PTOP-KILLS-2}")
                            .put(48, " &b3. &7{PTOP-KILLS-3}")
                            .put(49, " &b4. &7{PTOP-KILLS-4}")
                            .put(50, " &b5. &7{PTOP-KILLS-5}")
                            .put(51, " &b6. &7{PTOP-KILLS-6}")
                            .put(52, " &b7. &7{PTOP-KILLS-7}")
                            .put(53, " &b8. &7{PTOP-KILLS-8}")
                            .put(54, " &b9. &7{PTOP-KILLS-9}")
                            .put(55, " &b10. &7{PTOP-KILLS-10}")
                            .put(57, " &7Twoja pozycja: &b{POSITION-KILLS}")
                            .put(62, " &b&lTOP &8- &b&lZabojstwa")
                            .put(66, " &b1. &7{GTOP-KILLS-1}")
                            .put(67, " &b2. &7{GTOP-KILLS-2}")
                            .put(68, " &b3. &7{GTOP-KILLS-3}")
                            .put(69, " &b4. &7{GTOP-KILLS-4}")
                            .put(70, " &b5. &7{GTOP-KILLS-5}")
                            .put(71, " &b6. &7{GTOP-KILLS-6}")
                            .put(72, " &b7. &7{GTOP-KILLS-7}")
                            .put(73, " &b8. &7{GTOP-KILLS-8}")
                            .put(74, " &b9. &7{GTOP-KILLS-9}")
                            .put(75, " &b10. &7{GTOP-KILLS-10}")
                            .put(77, " &7Pozycja gildii: &b{G-POSITION-KILLS}")
                            .build(),
                            "&7GitHub: &agithub.com/funnyguilds",
                            "&c&lWiadomości braku (pokazujące się, gdy gracz nie ma gildii) można zmienić w plikach w katalogu &7&llang&c&l!"),

                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                            .put(42, " &b&lTOP &8- &b&lSmierci")
                            .put(46, " &b1. &7{PTOP-DEATHS-1}")
                            .put(47, " &b2. &7{PTOP-DEATHS-2}")
                            .put(48, " &b3. &7{PTOP-DEATHS-3}")
                            .put(49, " &b4. &7{PTOP-DEATHS-4}")
                            .put(50, " &b5. &7{PTOP-DEATHS-5}")
                            .put(51, " &b6. &7{PTOP-DEATHS-6}")
                            .put(52, " &b7. &7{PTOP-DEATHS-7}")
                            .put(53, " &b8. &7{PTOP-DEATHS-8}")
                            .put(54, " &b9. &7{PTOP-DEATHS-9}")
                            .put(55, " &b10. &7{PTOP-DEATHS-10}")
                            .put(57, " &7Twoja pozycja: &b{POSITION-DEATHS}")
                            .put(62, " &b&lTOP &8- &b&lSmierci")
                            .put(66, " &b1. &7{GTOP-DEATHS-1}")
                            .put(67, " &b2. &7{GTOP-DEATHS-2}")
                            .put(68, " &b3. &7{GTOP-DEATHS-3}")
                            .put(69, " &b4. &7{GTOP-DEATHS-4}")
                            .put(70, " &b5. &7{GTOP-DEATHS-5}")
                            .put(71, " &b6. &7{GTOP-DEATHS-6}")
                            .put(72, " &b7. &7{GTOP-DEATHS-7}")
                            .put(73, " &b8. &7{GTOP-DEATHS-8}")
                            .put(74, " &b9. &7{GTOP-DEATHS-9}")
                            .put(75, " &b10. &7{GTOP-DEATHS-10}")
                            .put(77, " &7Pozycja gildii: &b{G-POSITION-DEATHS}")
                            .build(),
                            "&7Strona: &6funnyguilds.dzikoysk.net",
                            "&c&lWiadomości braku (pokazujące się, gdy gracz nie ma gildii) można zmienić w plikach w katalogu &b&llang&c&l!")
            )
    );

    @Min(0)
    @Comment("")
    @Comment("Wartość pingu pokazana przy każdej komórce")
    public int cellsPing = 0;

    @Comment("")
    @Comment("Czy wszystkie możliwe komórki mają zostać zapełnione, niezależnie od liczby graczy online")
    public boolean fillCells = true;

    @Min(1)
    @Comment("")
    @Comment("Co ile ticków lista graczy powinna zostać odświeżona (20 ticków = 1 sekunda)")
    public int updateInterval = 20;

}
