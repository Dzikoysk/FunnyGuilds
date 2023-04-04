package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class DeleteCommand extends AbstractFunnyCommand {

    private final ConfirmCommand confirmExecutor;

    public DeleteCommand(FunnyGuilds plugin) throws Throwable {
        this.confirmExecutor = plugin.getInjector().newInstanceWithFields(ConfirmCommand.class);
    }

    @FunnyCommand(
            name = "${user.delete.name}",
            description = "${user.delete.description}",
            aliases = "${user.delete.aliases}",
            permission = "funnyguilds.delete",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild) {
        when(this.config.guildDeleteCancelIfSomeoneIsOnRegion && this.regionManager.isAnyUserInRegion(guild.getRegion().orNull(),
                guild.getMembers()), config -> config.guild.commands.delete.someoneNearby);
        ConfirmationList.add(owner.getUUID());

        when(this.config.commands.confirm.enabled, config -> config.guild.commands.delete.confirm);
        this.confirmExecutor.execute(owner, guild);
    }

}
