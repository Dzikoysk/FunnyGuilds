package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunnyComponent
public final class RankResetCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.rank-reset.name}",
            description = "${user.rank-reset.description}",
            aliases = "${user.rank-reset.aliases}",
            permission = "funnyguilds.rankreset",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user) {
        List<ItemStack> requiredItems = this.config.rankResetItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.player.commands.rankReset.missingItems)) {
            return;
        }

        int lastRank = user.getRank().getPoints();
        user.getRank().setPoints(this.config.rankStart);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{LAST-RANK}", lastRank)
                .register("{CURRENT-RANK}", user.getRank().getPoints());

        this.messageService.getMessage(config -> config.player.commands.rankReset.resetMessage)
                .receiver(player)
                .with(formatter)
                .send();
    }

}
