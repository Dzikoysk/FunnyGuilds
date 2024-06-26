package net.dzikoysk.funnyguilds.config.tablist;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class TablistPageSerializer implements ObjectSerializer<TablistPage> {

    @Override
    public boolean supports(@NotNull Class<? super TablistPage> type) {
        return TablistPage.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(TablistPage page, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("cycles", page.cycles);

        if (page.cells != null) {
            data.addAsMap("cells", page.cells, Integer.class, String.class);
        }

        if (page.header != null) {
            data.add("header", page.header);
        }

        if (page.footer != null) {
            data.add("footer", page.footer);
        }
    }

    @Override
    public TablistPage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        int cycles = data.get("cycles", Integer.class);
        Map<Integer, String> cells = data.containsKey("cells")
                ? data.getAsMap("cells", Integer.class, String.class)
                : null;
        String header = data.containsKey("header")
                ? data.get("header", String.class)
                : null;
        String footer = data.containsKey("footer")
                ? data.get("footer", String.class)
                : null;
        return new TablistPage(cycles, cells, header, footer);
    }

}
