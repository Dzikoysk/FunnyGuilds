package net.dzikoysk.funnyguilds.listener.region;

import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract extends AbstractFunnyListener {

    private final InfoCommand infoExecutor;

    public PlayerInteract(FunnyGuilds plugin) throws Throwable {
        this.infoExecutor = plugin.getInjector().newInstanceWithFields(InfoCommand.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action eventAction = event.getAction();
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (eventAction != Action.RIGHT_CLICK_BLOCK && eventAction != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (clicked == null) {
            return;
        }

        Region region = RegionUtils.getAt(clicked.getLocation());

        if (region == null) {
            return;
        }

        Block heart = region.getCenter().getBlock().getRelative(BlockFace.DOWN);

        if (clicked.equals(heart)) {
            if (heart.getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
            }

            Guild guild = region.getGuild();

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            event.setCancelled(true);

            if (eventAction == Action.LEFT_CLICK_BLOCK) {
                WarSystem.getInstance().attack(player, guild);
                return;
            }

            if (!config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                try {
                    infoExecutor.execute(player, new String[] {guild.getTag()});
                }
                catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(player::sendMessage);
                }
            }

            return;
        }

        if (eventAction == Action.RIGHT_CLICK_BLOCK) {
            Guild guild = region.getGuild();

            if (guild == null || guild.getName() == null) {
                return;
            }

            this.userManager.findByPlayer(player).peek(user -> {
                boolean blocked = config.blockedInteract.contains(clicked.getType());

                if (guild.getMembers().contains(user)) {
                    event.setCancelled(blocked && config.regionExplodeBlockInteractions && !guild.canBuild());
                }
                else {
                    event.setCancelled(blocked && !player.hasPermission("funnyguilds.admin.interact"));
                }
            });
        }

    }

}
