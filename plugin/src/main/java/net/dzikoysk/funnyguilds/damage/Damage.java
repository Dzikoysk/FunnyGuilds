package net.dzikoysk.funnyguilds.damage;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import net.dzikoysk.funnyguilds.shared.Validate;
import net.dzikoysk.funnyguilds.user.User;

public final class Damage {

    private final User attacker;
    private final double damage;
    private final Instant attackTime;

    private Damage(User attacker, double damage, Instant attackTime) {
        Validate.notNull(attacker, "Attacker cannot be null!");
        Validate.isTrue(damage >= 0, "Damage cannot be negative!");
        Validate.notNull(attackTime, "Attack time cannot be null!");

        this.attacker = attacker;
        this.damage = damage;
        this.attackTime = attackTime;
    }

    Damage(User attacker, double damage) {
        this(attacker, damage, Instant.now());
    }

    public User getAttacker() {
        return this.attacker;
    }

    public double getDamage() {
        return this.damage;
    }

    public Instant getAttackTime() {
        return this.attackTime;
    }

    public boolean isExpired(Duration expireTime) {
        return Duration.between(this.attackTime, Instant.now()).compareTo(expireTime) > 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.attacker, this.damage, this.attackTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Damage)) {
            return false;
        }
        Damage dmg = (Damage) obj;
        return Double.compare(dmg.damage, this.damage) == 0 && this.attacker.equals(dmg.attacker) && this.attackTime.equals(dmg.attackTime);
    }

}
