package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class PlayerInfoCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.player.name}",
        description = "${user.player.description}",
        aliases = "${user.player.aliases}",
        permission = "funnyguilds.playerinfo",
        completer = "online-players:3",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length == 0 && !(sender instanceof Player), this.messageConfiguration.playerOnly);
        
        String name = args.length == 0
                ? sender.getName()
                : args[0];

        User user = UserUtils.get(name, this.pluginConfiguration.playerLookupIgnorecase);
        when (user == null, this.messageConfiguration.generalNotPlayedBefore);

        sendInfoMessage(this.messageConfiguration.playerInfoList, user, sender);
    }
    
    public void sendInfoMessage(List<String> baseMessage, User infoUser, CommandSender messageTarget) {
        UserRank rank = infoUser.getRank();

        for (String messageLine : baseMessage) {
            if (infoUser.hasGuild()) {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", infoUser.getGuild().getName());
                messageLine = StringUtils.replace(messageLine, "{TAG}", infoUser.getGuild().getTag());
            }
            else {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", this.messageConfiguration.gNameNoValue);
                messageLine = StringUtils.replace(messageLine, "{TAG}", this.messageConfiguration.gTagNoValue);
            }

            messageLine = StringUtils.replace(messageLine, "{PLAYER}", infoUser.getName());
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.inRangeToString(rank.getPoints(), this.pluginConfiguration.pointsFormat));
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{ASSISTS}", Integer.toString(rank.getAssists()));
            messageLine = StringUtils.replace(messageLine, "{LOGOUTS}", Integer.toString(rank.getLogouts()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()));
            messageLine = StringUtils.replace(messageLine, "{RANK}", Integer.toString(rank.getPosition()));
            
            messageTarget.sendMessage(messageLine);
        }
    }

}
