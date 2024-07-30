package net.dzikoysk.funnyguilds.shared.adventure;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Collection;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEventSource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.stream.PandaStream;

public final class ItemComponentHelper {

    private ItemComponentHelper() {
    }

    public static Component itemAsComponent(ItemStack item, boolean displayAmount) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        Component itemComponent = Component.empty();
        if (displayAmount) {
            itemComponent = itemComponent.append(Component.text(item.getAmount() + config.itemAmountSuffix.getValue()));
        }

        Material material = item.getType();
        if (config.useTranslatableComponentsForMaterials) {
            itemComponent = itemComponent.append(Component.translatable(plugin.getLocaleManager().queryMaterial(material)));
        } else {
            itemComponent = itemComponent.append(Component.text(MaterialUtils.getMaterialName(material)));
        }

        if (config.enableItemComponent) {
            HoverEventSource<?> hoverEventSource;
            if (item instanceof HoverEventSource<?>) {
                hoverEventSource = (HoverEventSource<?>) item;
            } else {
                hoverEventSource = LegacyItemHoverEventHelper.getHoverForItem(item);
            }

            if (hoverEventSource != null) {
                itemComponent = itemComponent.hoverEvent(hoverEventSource);
            }
        }
        return itemComponent;
    }

    public static ItemReplacement prepareItemReplacement(ItemStack item) {
        return new ItemReplacement(item);
    }

    public static ItemsReplacement prepareItemsReplacement(Collection<ItemStack> items) {
        return new ItemsReplacement(items);
    }

    private static class ItemReplacement implements Replaceable {

        private final ItemStack item;

        private final TextReplacementConfig itemReplacement;
        private final TextReplacementConfig itemNoAmountReplacement;

        private ItemReplacement(ItemStack item) {
            this.item = item;

            this.itemReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEM}")
                    .replacement(itemAsComponent(this.item, true))
                    .build();
            this.itemNoAmountReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEM-NO-AMOUNT}")
                    .replacement(itemAsComponent(this.item, false))
                    .build();
        }

        @Override
        public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
            return new FunnyFormatter()
                    .register("{ITEM}", ItemUtils.itemAsString(this.item, true))
                    .register("{ITEM-NO-AMOUNT}", ItemUtils.itemAsString(this.item, false))
                    .format(text);
        }

        @Override
        public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
            return text.replaceText(this.itemReplacement).replaceText(this.itemNoAmountReplacement);
        }

    }

    private static class ItemsReplacement implements Replaceable {

        private final Collection<ItemStack> items;

        private final TextReplacementConfig itemsReplacement;
        private final TextReplacementConfig itemsNoAmountReplacement;

        private ItemsReplacement(Collection<ItemStack> items) {
            this.items = items;

            this.itemsReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEMS}")
                    .replacement(Component.join(JoinConfiguration.commas(true), PandaStream.of(this.items)
                            .map(itemStack -> itemAsComponent(itemStack, true))
                            .toList()))
                    .build();
            this.itemsNoAmountReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEMS-NO-AMOUNT}")
                    .replacement(Component.join(JoinConfiguration.commas(true), PandaStream.of(this.items)
                            .map(itemStack -> itemAsComponent(itemStack, false))
                            .toList()))
                    .build();
        }

        @Override
        public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
            return new FunnyFormatter()
                    .register("{ITEMS}", FunnyStringUtils.join(PandaStream.of(this.items)
                            .map(itemStack -> ItemUtils.itemAsString(itemStack, true))
                            .toList(), true))
                    .register("{ITEMS-NO-AMOUNT}", FunnyStringUtils.join(PandaStream.of(this.items)
                            .map(itemStack -> ItemUtils.itemAsString(itemStack, false))
                            .toList(), true))
                    .format(text);
        }

        @Override
        public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
            return text.replaceText(this.itemsReplacement).replaceText(this.itemsNoAmountReplacement);
        }

    }

}
