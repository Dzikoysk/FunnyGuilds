package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Collections;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class EnlargeCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.enlarge.name}",
            description = "${user.enlarge.description}",
            aliases = "${user.enlarge.aliases}",
            permission = "funnyguilds.enlarge",
            playerOnly = true
    )
    public void execute(Player player, @CanManage User deputy, Guild guild) {
        Region region = when(guild.getRegion(), config -> config.guild.region.disabled);

        int currentEnlargementLevel = region.getEnlargementLevel();
        when(currentEnlargementLevel > this.config.enlargeItems.size() - 1, config -> config.guild.commands.enlarge.maxSize);

        ItemStack need = this.config.enlargeItems.get(currentEnlargementLevel);
        if (!ItemUtils.playerHasEnoughItems(player, Collections.singletonList(need), config -> config.guild.commands.enlarge.missingItems)) {
            return;
        }

        when(this.regionManager.isNearRegion(region.getCenter()), config -> config.guild.commands.enlarge.nearOtherGuild);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, deputy, guild))) {
            return;
        }

        player.getInventory().removeItem(need);
        this.regionManager.changeRegionEnlargement(region, currentEnlargementLevel + 1);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{SIZE}", region.getSize())
                .register("{LEVEL}", region.getEnlargementLevel());

        this.messageService.getMessage(config -> config.guild.commands.enlarge.enlarged)
                .receiver(guild)
                .with(formatter)
                .send();
    }

}
