package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

public class ExcEnlarge implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!config.enlargeEnable) {
            return;
        }

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        Region region = Region.get(user.getGuild().getRegion());
        int enlarge = region.getEnlarge();

        if (enlarge > config.enlargeItems.size() - 1) {
            player.sendMessage(messages.enlargeMaxSize);
            return;
        }

        ItemStack need = config.enlargeItems.get(enlarge);

        if (!player.getInventory().containsAtLeast(need, need.getAmount())) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(need.getAmount());
            messageBuilder.append(" ");
            messageBuilder.append(need.getType().toString().toLowerCase());

            player.sendMessage(messages.enlargeItem.replace("{ITEM}", messageBuilder.toString()));
            return;
        }

        if (RegionUtils.isNear(region.getCenter())) {
            player.sendMessage(messages.enlargeIsNear);
            return;
        }

        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + config.enlargeSize);

        String enlargeDoneMessage = messages.enlargeDone
                .replace("{SIZE}", Integer.toString(region.getSize()))
                .replace("{LEVEL}", Integer.toString(region.getEnlarge()));

        user.getGuild().broadcast(enlargeDoneMessage);
    }
}
