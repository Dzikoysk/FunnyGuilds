package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.fight.FightUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (!(entity instanceof Player)) {
            return;
        }

        Player attacker = null;
        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile) {
            @SuppressWarnings("deprecation")
            LivingEntity le = (LivingEntity) ((Projectile) damager).getShooter();
            if (le instanceof Player) {
                attacker = (Player) le;
            }
        }

        if (attacker == null) {
            return;
        }

        Player victim = (Player) event.getEntity();

        User uv = User.get(victim);
        User ua = User.get(attacker);

        if (uv.hasGuild() && ua.hasGuild()) {
            if (uv.getUUID().equals(ua.getUUID())) {
                return;
            }
            if (uv.getGuild().equals(ua.getGuild())) {
                if (!uv.getGuild().getPvP()) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (uv.getGuild().getAllies().contains(ua.getGuild())) {
                if (!Settings.getConfig().damageAlly) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        FightUtils.attacked(uv);
    }
}
