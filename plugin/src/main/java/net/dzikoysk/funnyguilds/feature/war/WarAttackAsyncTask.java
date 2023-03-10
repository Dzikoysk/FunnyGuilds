package net.dzikoysk.funnyguilds.feature.war;

import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
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

public class WarAttackAsyncTask extends AsyncFunnyTask {

    private final FunnyServer funnyServer;
    private final GuildEntityHelper guildEntityHelper;
    private final User user;
    private final int entityId;

    public WarAttackAsyncTask(FunnyServer funnyServer, GuildEntityHelper guildEntityHelper, User user, int entityId) {
        this.funnyServer = funnyServer;
        this.guildEntityHelper = guildEntityHelper;
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
                .forEach(playerToGuild -> this.attackGuild(playerToGuild.getFirst(), playerToGuild.getSecond()));
    }

    private void attackGuild(Player player, Guild guild) {
        GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(
                EventCause.USER,
                this.user,
                guild,
                Click.LEFT,
                !SecuritySystem.onHitCrystal(player, guild)
        );
        SimpleEventHandler.handle(interactEvent);

        if (interactEvent.isCancelled()) {
            return;
        }

        if (!interactEvent.isSecurityCheckPassed()) {
            FunnyGuilds.getPluginLogger().debug("Security check failed for " + player.getName() + " when attacking " + guild.getName());
            return;
        }

        WarSystem.getInstance().attack(player, guild);
    }

}
