package net.dzikoysk.funnyguilds.config.requirement;

import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class RankRequirementsComponent extends PriorityRequirementsComponent {

    private int ranking = 0;

    public RankRequirementsComponent(int priority, Set<ItemStack> items) {
        super(priority, items);
    }

    public RankRequirementsComponent(int priority) {
        super(priority);
    }

    private RankRequirementsComponent() {
    }

    public int getRanking() {
        return this.ranking;
    }

}
