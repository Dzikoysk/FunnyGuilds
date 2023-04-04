package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunnyComponent
public final class StatsResetCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.stats-reset.name}",
            description = "${user.stats-reset.description}",
            aliases = "${user.stats-reset.aliases}",
            permission = "funnyguilds.statsreset",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user) {
        List<ItemStack> requiredItems = this.config.statsResetItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.player.commands.statsReset.missingItems)) {
            return;
        }

        UserRank rank = user.getRank();

        int lastPoints = rank.getPoints();
        int lastKills = rank.getKills();
        int lastDeaths = rank.getDeaths();
        int lastAssists = rank.getAssists();
        int lastLogouts = rank.getLogouts();

        rank.setPoints(this.config.rankStart);
        rank.setKills(0);
        rank.setDeaths(0);
        rank.setAssists(0);
        rank.setLogouts(0);

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{LAST-POINTS}", lastPoints)
                .register("{CURRENT-POINTS}", rank.getPoints())
                .register("{LAST-DEATHS}", lastDeaths)
                .register("{CURRENT-DEATHS}", rank.getDeaths())
                .register("{LAST-KILLS}", lastKills)
                .register("{CURRENT-KILLS}", rank.getKills())
                .register("{LAST-ASSISTS}", lastAssists)
                .register("{CURRENT-ASSISTS}", rank.getAssists())
                .register("{LAST-LOGOUTS}", lastLogouts)
                .register("{CURRENT-LOGOUTS}", rank.getLogouts());

        this.messageService.getMessage(config -> config.player.commands.statsReset.resetMessage)
                .receiver(player)
                .with(formatter)
                .send();
    }

}
