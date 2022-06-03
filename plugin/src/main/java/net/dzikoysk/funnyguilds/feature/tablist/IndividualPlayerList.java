package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Joiner;

public class IndividualPlayerList {

    private final User user;
    private final PlayerList playerList;

    private final Map<Integer, String> unformattedCells;
    private final int cellCount;
    private final String header;
    private final String footer;

    private final boolean animated;
    private final List<TablistPage> pages;
    private final int pagesCount;

    private final Map<NumberRange, SkinTexture> cellTextures;
    private final int cellPing;

    private final boolean enableLegacyPlaceholders;

    private int cycle;
    private int currentPage;

    public IndividualPlayerList(User user, PlayerListAccessor playerListAccessor, Map<Integer, String> unformattedCells, String header,
                                String footer, boolean animated, List<TablistPage> pages, Map<NumberRange, SkinTexture> cellTextures,
                                int cellPing, boolean fillCells, boolean enableLegacyPlaceholders) {
        this.user = user;

        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.animated = animated;
        this.pages = pages;
        this.pagesCount = pages.size();
        this.cellTextures = cellTextures;
        this.cellPing = cellPing;

        this.enableLegacyPlaceholders = enableLegacyPlaceholders;

        if (!fillCells) {
            Entry<Integer, String> entry = MapUtil.findTheMaximumEntryByKey(unformattedCells);
            if (entry != null) {
                this.cellCount = entry.getKey();
            }
            else {
                this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
            }
        }
        else {
            this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
        }

        this.playerList = playerListAccessor.createPlayerList(this.cellCount);
    }

    public void send() {
        Map<Integer, String> unformattedCells = this.unformattedCells;
        String header = this.header;
        String footer = this.footer;

        if (this.animated) {
            this.cycle++;

            int pageCycles = this.pages.get(this.currentPage).cycles;
            if (this.cycle + 1 >= pageCycles) {
                this.cycle = 0;
                this.currentPage++;

                if (this.currentPage >= this.pagesCount) {
                    this.currentPage = 0;
                }
            }

            TablistPage page = this.pages.get(this.currentPage);
            if (page != null) {
                if (page.playerList != null) {
                    unformattedCells.putAll(page.playerList);
                }

                if (page.playerListHeader != null) {
                    header = page.playerListHeader;
                }

                if (page.playerListFooter != null) {
                    footer = page.playerListFooter;
                }
            }
        }

        String[] preparedCells = this.putVarsPrepareCells(unformattedCells, header, footer);
        String preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
        String preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

        SkinTexture[] preparedCellsTextures = this.putTexturePrepareCells();

        Option.of(Bukkit.getPlayer(this.user.getUUID())).peek(player -> {
            this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, preparedCellsTextures, this.cellPing, Collections.emptySet());
        });
    }

    private String[] putVarsPrepareCells(Map<Integer, String> tablistPattern, String header, String footer) {
        String[] allCells = new String[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
        for (int i = 0; i < this.cellCount; i++) {
            allCells[i] = this.putTop(tablistPattern.getOrDefault(i + 1, ""));
        }

        allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = header;
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = footer;

        String mergedCells = Joiner.on("\0").join(allCells).toString();
        return StringUtils.splitPreserveAllTokens(this.putVars(mergedCells), '\0');
    }

    private String putTop(String cell) {
        return FunnyGuilds.getInstance().getRankPlaceholdersService().format(cell, this.user);
    }

    private String putVars(String cell) {
        String formatted = cell;

        Option<Player> playerOption = this.user.getPlayer();
        if (playerOption.isEmpty()) {
            return formatted;
        }

        Player player = playerOption.get();

        formatted = FunnyGuilds.getInstance().getTablistPlaceholdersService().format(formatted, this.user);
        formatted = ChatUtils.colored(formatted);
        formatted = HookUtils.replacePlaceholders(player, formatted);

        return formatted;
    }

    public SkinTexture[] putTexturePrepareCells() {
        SkinTexture[] textures = new SkinTexture[PlayerListConstants.DEFAULT_CELL_COUNT];

        this.cellTextures.forEach((range, texture) -> {
            for (int i = range.getMinRange().intValue(); i <= range.getMaxRange().intValue(); i++) {
                textures[i - 1] = texture;
            }
        });

        return textures;
    }

}
