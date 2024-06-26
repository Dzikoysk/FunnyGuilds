package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class NameCommand extends AbstractFunnyCommand {

    @Inject
    public DataModel dataModel;

    @FunnyCommand(
            name = "${admin.name.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.commands.validation.noTagGiven);
        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        when(args.length < 2, config -> config.admin.commands.guild.name.noValueGiven);
        when(this.guildManager.nameExists(args[1]), config -> config.commands.validation.guildWithNameExists);

        User admin = AdminUtils.getAdminUser(sender);
        String oldName = guild.getName();
        if (!SimpleEventHandler.handle(new GuildPreRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]))) {
            return;
        }

        guild.getRegion().peek(region -> {
            if (this.dataModel instanceof FlatDataModel) {
                FlatDataModel dataModel = (FlatDataModel) this.dataModel;
                dataModel.getRegionFile(region).peek(FunnyIOUtils::deleteFile);
            }
            else if (this.dataModel instanceof SQLDataModel) {
                DatabaseRegionSerializer.delete(region);
            }

            region.setName(args[1]);
        });

        if (this.dataModel instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) this.dataModel;
            dataModel.getGuildFile(guild).peek(FunnyIOUtils::deleteFile);
        }
        else if (this.dataModel instanceof SQLDataModel) {
            DatabaseGuildSerializer.delete(guild);
        }

        guild.setName(args[1]);
        this.messageService.getMessage(config -> config.admin.commands.guild.name.changed)
                .receiver(sender)
                .with(guild)
                .with("{OLD_NAME}", oldName)
                .send();

        SimpleEventHandler.handle(new GuildRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]));
    }

}
