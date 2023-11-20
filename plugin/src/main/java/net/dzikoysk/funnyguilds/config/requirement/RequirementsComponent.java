package net.dzikoysk.funnyguilds.config.requirement;

import eu.okaeri.validator.annotation.Min;
import java.util.Set;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import org.bukkit.inventory.ItemStack;

public class RequirementsComponent extends ConfigSection {

    private Set<ItemStack> items = Set.of();
    private int exp = 0;
    private double money = 0;

    public RequirementsComponent(Set<ItemStack> items) {
        this.items = items;
    }

    public RequirementsComponent() {
    }

    public Set<ItemStack> getItems() {
        return this.items;
    }

    public int getExp() {
        return this.exp;
    }

    public double getMoney() {
        return this.money;
    }

    public boolean matchRequirements() {
        return false; //TODO
    }

}
