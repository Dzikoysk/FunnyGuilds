package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class ConfirmCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.confirm.name}",
            description = "${user.confirm.description}",
            aliases = "${user.confirm.aliases}",
            permission = "funnyguilds.delete",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild) {
        when(this.config.guildDeleteCancelIfSomeoneIsOnRegion && this.regionManager.isAnyUserInRegion(guild.getRegion().orNull(), guild.getMembers()), config -> config.guild.commands.delete.someoneNearby);
        when(!ConfirmationList.contains(owner.getUUID()), config -> config.guild.commands.delete.notingToConfirm);

        ConfirmationList.remove(owner.getUUID());

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.USER, owner, guild))) {
            return;
        }

        this.guildManager.deleteGuild(this.plugin, guild);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", owner.getName());

        this.messageService.getMessage(config -> config.guild.commands.delete.deleted)
                .receiver(owner)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.delete.deletedBroadcast)
                .broadcast()
                .with(formatter)
                .send();
    }

}
