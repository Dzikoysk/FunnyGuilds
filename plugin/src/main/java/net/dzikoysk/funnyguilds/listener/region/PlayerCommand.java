package net.dzikoysk.funnyguilds.listener.region;

import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerCommand extends AbstractFunnyListener {

    private static final Pattern COMMAND_SPLIT_PATTERN = Pattern.compile("\\s+");

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("funnyguilds.admin")) {
            return;
        }

        String[] commandElements = COMMAND_SPLIT_PATTERN.split(event.getMessage());
        if (commandElements.length == 0) {
            return;
        }

        String command = commandElements[0].substring(1);
        PandaStream.of(this.config.regionCommands)
                .find(blockedCommand -> blockedCommand.equalsIgnoreCase(command))
                .peek(blockedCommand -> this.handleCommandUsage(player, event));
    }

    private void handleCommandUsage(Player player, Cancellable event) {
        Option<Guild> guild = this.regionManager.findRegionAtLocation(player.getLocation()).map(Region::getGuild);
        if (guild.isEmpty()) {
            return;
        }

        Option<User> user = this.userManager.findByPlayer(player);
        if (user.isEmpty() || guild.get().isMember(user.get())) {
            return;
        }

        event.setCancelled(true);
        this.messageService.getMessage(config -> config.guild.region.protection.command)
                .receiver(player)
                .send();
    }

}
