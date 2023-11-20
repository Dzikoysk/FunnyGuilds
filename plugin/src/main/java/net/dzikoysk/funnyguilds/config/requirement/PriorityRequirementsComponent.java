package net.dzikoysk.funnyguilds.config.requirement;

import eu.okaeri.validator.annotation.Min;
import java.util.Set;
import net.dzikoysk.funnyguilds.config.ConfigSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PriorityRequirementsComponent extends RequirementsComponent implements Comparable<PriorityRequirementsComponent> {

    @Min(0)
    private byte priority = 0;

    private Set<ItemStack> items = Set.of();
    private int exp = 0;
    private double money = 0;

    public PriorityRequirementsComponent(int priority, Set<ItemStack> items) {
        super(items);
        this.priority = (byte) priority;
    }

    public PriorityRequirementsComponent(int priority) {
        this.priority = (byte) priority;
    }

    PriorityRequirementsComponent() {
    }

    public byte getPriority() {
        return this.priority;
    }

    @Override
    public int compareTo(@NotNull PriorityRequirementsComponent o) {
        return Byte.compare(this.priority, o.priority);
    }

}
