package net.dzikoysk.funnyguilds.feature.security.cheat;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.security.SecurityUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.config.SecuritySystemConfiguration;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import panda.utilities.text.Joiner;

public final class SecurityFreeCam {

    private SecurityFreeCam() {
    }

    public static void on(Player player, Guild guild, Vector origin, Vector hitPoint, double distance) {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        SecuritySystemConfiguration.FreeCam config = funnyGuilds.getPluginConfiguration().securitySystem.freeCam;
        UserManager userManager = funnyGuilds.getUserManager();

        Vector directionToHitPoint = hitPoint.clone().subtract(origin);
        BlockIterator blockIterator = new BlockIterator(player.getWorld(), origin, directionToHitPoint, 0, Math.max((int) distance, 1));

        List<Block> blocks = StreamSupport.stream(Spliterators.spliteratorUnknownSize(blockIterator, Spliterator.NONNULL | Spliterator.IMMUTABLE), false)
                .filter(block -> !block.isLiquid())
                .filter(block -> block.getType().isSolid())
                .filter(block -> block.getType().isOccluding() || block.getType() == Material.GLASS)
                .limit(8)
                .collect(Collectors.toList());
        guild.getEnderCrystal().map(Location::getBlock).peek(blocks::remove);

        int compensationSneaking = player.isSneaking() ? 1 : 0;
        if (blocks.size() <= config.compensation + compensationSneaking) {
            return;
        }

        String blocksString = Joiner.on(", ").join(blocks, b -> MaterialUtils.getMaterialName(b.getType())).toString();

        SecurityUtils.addViolationLevel(userManager.findByPlayer(player).orNull());
        SecurityUtils.sendToOperator(player, CheatType.FREE_CAM, Replacement.of("{BLOCKS}", blocksString));
    }

}
