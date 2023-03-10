package net.dzikoysk.funnyguilds.shared.bukkit;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.StringReplacer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public final class ItemUtils {

    private ItemUtils() {
    }

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems, Function<MessageConfiguration, Sendable> messageSupplier) {
        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            FunnyGuilds.getInstance().getMessageService().getMessage(messageSupplier)
                    .receiver(player)
                    .with(ItemComponentHelper.prepareItemReplacement(requiredItem))
                    .with(ItemComponentHelper.prepareItemsReplacement(requiredItems))
                    .send();

            return false;
        }
        return true;
    }

    public static String translateTextPlaceholder(String text, Collection<ItemStack> items, ItemStack item) {
        return StringReplacer.replace(
                text,
                ItemComponentHelper.prepareItemReplacement(item),
                ItemComponentHelper.prepareItemsReplacement(items)
        );
    }

    public static String itemAsString(ItemStack item, boolean displayAmount) {
        String materialName = MaterialUtils.getMaterialName(item.getType());
        if (!displayAmount) {
            return materialName;
        }
        return item.getAmount() + FunnyGuilds.getInstance().getPluginConfiguration().itemAmountSuffix.getValue() + materialName;
    }

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");

        Option<Integer> amount = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[0])).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown amount: " + split[0]);
        });
        Material material = MaterialUtils.parseMaterial(split[1], false);

        ItemBuilder item = new ItemBuilder(material, amount.orElseGet(1));
        FunnyFormatter formatter = new FunnyFormatter().register("_", " ").register("{HASH}", "#");

        for (int index = 2; index < split.length; index++) {
            String[] itemAttributes = split[index].split(":", 2);

            if (itemAttributes.length != 2) {
                FunnyGuilds.getPluginLogger().parser("Unknown item meta attribute: " + itemAttributes[0]);
                continue;
            }

            String attributeName = itemAttributes[0];
            String attributeValue = itemAttributes[1];

            switch (attributeName.toLowerCase(Locale.ROOT)) {
                case "damage":
                case "durability":
                    if (!(item.getMeta() instanceof Damageable)) {
                        FunnyGuilds.getPluginLogger().parser("Cannot set durability for " + material.name());
                        continue;
                    }

                    Option<Integer> damage = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(attributeValue)).onEmpty(() -> {
                        FunnyGuilds.getPluginLogger().parser("Unknown durability: " + attributeValue);
                    });

                    ((Damageable) item.getMeta()).setDamage(damage.orElseGet(0));
                    continue;
                case "name":
                case "displayname":
                    item.setName(formatter.format(attributeValue), true);
                    continue;
                case "lore":
                    List<String> lore = PandaStream.of(attributeValue.split("#")).map(formatter::format).toList();
                    item.setLore(lore, true);
                    continue;
                case "enchant":
                case "enchantment":
                    Pair<Enchantment, Integer> parsedEnchant = parseEnchant(attributeValue);
                    if (parsedEnchant.getFirst() == null) {
                        continue;
                    }

                    item.addEnchant(parsedEnchant.getFirst(), parsedEnchant.getSecond());
                    continue;
                case "enchants":
                case "enchantments":
                    PandaStream.of(attributeValue.split(","))
                            .map(ItemUtils::parseEnchant)
                            .filter(enchant -> enchant.getFirst() != null)
                            .forEach(enchant -> item.addEnchant(enchant.getFirst(), enchant.getSecond()));
                    continue;
                case "skullowner":
                    if (!(item.getMeta() instanceof SkullMeta)) {
                        FunnyGuilds.getPluginLogger().parser("Invalid item skull owner attribute (given item is not a skull!): " + split[index]);
                        continue;
                    }

                    ((SkullMeta) item.getMeta()).setOwner(attributeValue);
                    item.refreshMeta();
                    continue;
                case "flags":
                case "itemflags":
                    PandaStream.of(attributeValue.split(","))
                            .map(String::trim)
                            .mapOpt(ItemUtils::matchItemFlag)
                            .forEach(item::setFlag);

                    continue;
                case "armorcolor":
                    if (!(item.getMeta() instanceof LeatherArmorMeta)) {
                        FunnyGuilds.getPluginLogger().parser("Invalid item armor color attribute (given item is not a leather armor!): " + split[index]);
                        continue;
                    }

                    String[] colorSplit = attributeValue.split("_");

                    try {
                        Color color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                        ((LeatherArmorMeta) item.getMeta()).setColor(color);
                        item.refreshMeta();
                    }
                    catch (NumberFormatException numberFormatException) {
                        FunnyGuilds.getPluginLogger().parser("Invalid armor color: " + attributeValue);
                    }
            }
        }

        return item.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return PandaStream.of(itemStrings).map(ItemUtils::parseItem).toList();
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static String toString(ItemStack item) {
        int amount = item.getAmount();
        String material = item.getType().toString().toLowerCase(Locale.ROOT);

        StringBuilder itemString = new StringBuilder(amount + " " + material);
        FunnyFormatter formatter = new FunnyFormatter().register(" ", "_").register("#", "{HASH}");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return itemString.toString();
        }

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            itemString.append(" damage:").append(damageable.getDamage());
        }

        if (meta.hasDisplayName()) {
            itemString.append(" name:").append(formatter.format(ChatUtils.decolor(meta.getDisplayName())));
        }

        if (meta.hasLore()) {
            List<String> lore = PandaStream.of(meta.getLore())
                    .map(ChatUtils::decolor)
                    .map(formatter::format)
                    .toList();

            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = PandaStream.of(meta.getEnchants().entrySet().stream())
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase(Locale.ROOT) + ":" + entry.getValue())
                    .toList();

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = PandaStream.of(meta.getItemFlags())
                    .map(ItemFlag::name)
                    .map(name -> name.toLowerCase(Locale.ROOT))
                    .toList();

            itemString.append(" flags:").append(Joiner.on(",").join(flags));
        }

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullMeta.hasOwner()) {
                itemString.append(" skullowner:").append(skullMeta.getOwner());
            }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            Color color = armorMeta.getColor();

            String colorString = color.getRed() + "_" + color.getGreen() + "_" + color.getBlue();
            itemString.append(" armorcolor:").append(colorString);
        }

        return itemString.toString();
    }

    private static Enchantment matchEnchant(String enchantName) {
        return Enchantment.getByKey(NamespacedKey.minecraft(enchantName.toLowerCase(Locale.ROOT)));
    }

    private static String getEnchantName(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");

        Enchantment enchant = matchEnchant(split[0]);
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }

        Option<Integer> level = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[1]));
        if (level.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
        }

        return Pair.of(enchant, level.orElseGet(1));
    }

    private static Option<ItemFlag> matchItemFlag(String flagName) {
        return Option.attempt(IllegalArgumentException.class, () -> {
            return ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flagName);
        });
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        return PandaStream.of(inv.getContents())
                .filter(item::isSimilar)
                .toStream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

}
