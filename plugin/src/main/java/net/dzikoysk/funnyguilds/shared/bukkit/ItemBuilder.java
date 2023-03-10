package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import panda.std.stream.PandaStream;

public final class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int stack) {
        this.itemStack = new ItemStack(material, stack);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public void refreshMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    public ItemBuilder setName(String name, boolean color) {
        this.itemMeta.setDisplayName(color ? ChatUtils.colored(name) : name);
        this.refreshMeta();

        return this;
    }

    public ItemBuilder setLore(Iterable<String> lore, boolean color) {
        this.itemMeta.setLore(PandaStream.of(lore).map(line -> color ? ChatUtils.colored(line) : line).toList());
        this.refreshMeta();

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore), true);
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.itemMeta.addEnchant(enchant, level, true);
        this.refreshMeta();

        return this;
    }

    public ItemBuilder setFlag(ItemFlag flag) {
        this.itemMeta.addItemFlags(flag);
        this.refreshMeta();

        return this;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public ItemMeta getMeta() {
        return this.itemMeta;
    }

}
