package net.dzikoysk.funnyguilds.feature.war;

import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class WarInfoAsyncTask extends AsyncFunnyTask {

    private final FunnyServer funnyServer;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final GuildEntityHelper guildEntityHelper;
    private InfoCommand infoExecutor;
    private final User user;
    private final int entityId;

    public WarInfoAsyncTask(FunnyGuilds plugin, GuildEntityHelper guildEntityHelper, User user, int entityId) {
        this.funnyServer = plugin.getFunnyServer();
        this.config = plugin.getPluginConfiguration();
        this.messageService = plugin.getMessageService();
        this.guildEntityHelper = guildEntityHelper;

        try {
            this.infoExecutor = plugin.getInjector().newInstanceWithFields(InfoCommand.class);
        }
        catch (Throwable throwable) {
            FunnyGuilds.getPluginLogger().error("An error occurred while creating war info request", throwable);
        }

        this.user = user;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        PandaStream.of(this.guildEntityHelper.getGuildEntities().entrySet())
                .filter(entry -> entry.getValue().id() == this.entityId)
                .map(Entry::getKey)
                .mapOpt(guild -> this.funnyServer.getPlayer(this.user)
                        .map(player -> Pair.of(player, guild))
                )
                .filter(playerToGuild -> playerToGuild.getSecond()
                        .getEnderCrystal()
                        .map(Location::getWorld)
                        .is(guildWorld -> guildWorld.equals(playerToGuild.getFirst().getWorld())))
                .forEach(playerToGuild -> this.displayGuildInfo(playerToGuild.getFirst(), playerToGuild.getSecond()));
    }

    private void displayGuildInfo(Player player, Guild guild) {
        GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(
                EventCause.USER,
                this.user,
                guild,
                Click.RIGHT,
                !SecuritySystem.onHitCrystal(player, guild)
        );

        SimpleEventHandler.handle(interactEvent);
        if (interactEvent.isCancelled() || !interactEvent.isSecurityCheckPassed()) {
            return;
        }

        if (this.config.informationMessageCooldowns.cooldown(player.getUniqueId(), this.config.infoPlayerCooldown)) {
            return;
        }

        try {
            this.infoExecutor.execute(player, new String[] {guild.getTag()});
        }
        catch (InternalValidationException validatorException) {
            this.messageService.getMessage(validatorException.getMessageSupplier())
                    .receiver(player)
                    .with(validatorException.getReplacements())
                    .send();
        }
    }

}
