package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class SetBaseCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.set-base.name}",
            description = "${user.set-base.description}",
            aliases = "${user.set-base.aliases}",
            permission = "funnyguilds.setbase",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @CanManage User deputy, Guild guild) {
        when(!this.config.regionsEnabled, config -> config.guild.region.disabled);

        Region region = this.regionManager.findByName(guild.getName()).orNull();
        Location location = player.getLocation();
        when(region == null || !region.isIn(location), config -> config.guild.commands.setBase.outsideRegion);

        if (!SimpleEventHandler.handle(new GuildBaseChangeEvent(EventCause.USER, deputy, guild, location))) {
            return;
        }

        guild.setHome(location);

        if (location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            for (int y = location.getBlockY(); y > 0; y--) {
                location.setY(y);

                if (location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                    break;
                }
            }
        }

        this.messageService.getMessage(config -> config.guild.commands.setBase.set)
                .receiver(deputy)
                .send();
    }

}
