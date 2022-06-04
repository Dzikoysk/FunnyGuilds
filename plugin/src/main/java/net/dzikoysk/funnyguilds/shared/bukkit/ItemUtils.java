package net.dzikoysk.funnyguilds.shared.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.EggTypeChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.spigot.ItemComponentUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public final class ItemUtils {

    private static Method BY_IN_GAME_NAME_ENCHANT;
    private static Method CREATE_NAMESPACED_KEY;

    private static Method GET_IN_GAME_NAME_ENCHANT;
    private static Method GET_NAMESPACED_KEY;

    static {
        if (!Reflections.USE_PRE_12_METHODS) {
            Class<?> namespacedKeyClass = Reflections.getBukkitClass("NamespacedKey");

            BY_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getByKey");
            CREATE_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "minecraft", String.class);

            GET_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getKey");
            GET_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "getKey");
        }
    }

    private ItemUtils() {
    }

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems, String message) {
        boolean enableItemComponent = FunnyGuilds.getInstance().getPluginConfiguration().enableItemComponent;

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (message.isEmpty()) {
                return false;
            }

            if (enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(message, requiredItems, requiredItem));
            }
            else {
                player.sendMessage(translateTextPlaceholder(message, requiredItems, requiredItem));
            }

            return false;
        }

        return true;
    }

    public static String translateTextPlaceholder(String message, Collection<ItemStack> items, ItemStack item) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        FunnyFormatter formatter = new FunnyFormatter();

        if (message.contains("{ITEM}")) {
            formatter.register("{ITEM}", item.getAmount() + config.itemAmountSuffix.getValue() + " " +
                    MaterialUtils.getMaterialName(item.getType()));
        }

        if (message.contains("{ITEMS}")) {
            formatter.register("{ITEMS}", FunnyStringUtils.join(
                    PandaStream.of(items)
                            .map(itemStack -> itemStack.getAmount() + config.itemAmountSuffix.getValue() + " " +
                                    MaterialUtils.getMaterialName(itemStack.getType()))
                            .collect(Collectors.toList()), true)
            );
        }

        if (message.contains("{ITEM-NO-AMOUNT}")) {
            formatter.register("{ITEM-NO-AMOUNT}", MaterialUtils.getMaterialName(item.getType()));
        }

        return formatter.format(message);
    }

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material material = MaterialUtils.parseMaterial(typeSplit[0], false);

        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        }
        catch (NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser("Unknown size: " + split[0]);
            stack = 1;
            data = 0;
        }

        ItemBuilder item = new ItemBuilder(material, stack, data);
        FunnyFormatter formatter = new FunnyFormatter().register("_", "").register("{HASH}", "#");

        for (int index = 2; index < split.length; index++) {
            String[] itemAttribute = split[index].split(":", 2);

            String attributeName = itemAttribute[0];
            String attributeValue = itemAttribute[1];

            switch (attributeName.toLowerCase()) {
                case "name":
                case "displayname":
                    item.setName(formatter.format(attributeName), true);
                    continue;
                case "lore":
                    String[] loreLines = String.join(":", attributeValue).split("#");
                    List<String> lore = Arrays.stream(loreLines).map(formatter::format).collect(Collectors.toList());

                    item.setLore(lore, true);
                    continue;
                case "enchant":
                case "enchantment":
                    Pair<Enchantment, Integer> parsedEnchant = parseEnchant(attributeValue);
                    item.addEnchant(parsedEnchant.getFirst(), parsedEnchant.getSecond());
                    continue;
                case "enchants":
                case "enchantments":
                    Arrays.stream(attributeValue.split(","))
                            .map(ItemUtils::parseEnchant)
                            .filter(enchant -> enchant.getFirst() != null)
                            .forEach(enchant -> item.addEnchant(enchant.getFirst(), enchant.getSecond()));
                    continue;
                case "skullowner":
                    if (item.getMeta() instanceof SkullMeta) {
                        ((SkullMeta) item.getMeta()).setOwner(attributeValue);
                        item.refreshMeta();
                    }

                    continue;
                case "flags":
                case "itemflags":
                    String[] flags = attributeValue.split(",");

                    for (String flag : flags) {
                        flag = flag.trim();

                        Option<ItemFlag> matchedFlag = matchItemFlag(flag);
                        if (matchedFlag.isEmpty()) {
                            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flag);
                            continue;
                        }

                        item.setFlag(matchedFlag.get());
                    }

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

                    continue;
                case "eggtype":
                    if (!EggTypeChanger.needsSpawnEggMeta()) {
                        FunnyGuilds.getPluginLogger().info("This MC version supports metadata for spawnGuildHeart egg type, " +
                                "no need to use eggtype in item creation!");
                        continue;
                    }

                    EntityType type = null;
                    String entityTypeName = attributeValue.toUpperCase();

                    try {
                        type = EntityType.valueOf(entityTypeName);
                    }
                    catch (Exception exception) {
                        FunnyGuilds.getPluginLogger().parser("Unknown entity type: " + entityTypeName);
                    }

                    if (type != null) {
                        EggTypeChanger.applyChanges(item.getMeta(), type);
                        item.refreshMeta();
                    }
            }
        }

        return item.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return itemStrings.stream().map(ItemUtils::parseItem).collect(Collectors.toList());
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static String toString(ItemStack item) {
        String material = item.getType().toString().toLowerCase();
        short durability = item.getDurability();
        int amount = item.getAmount();

        StringBuilder itemString = new StringBuilder(amount + " " + material + (durability > 0 ? ":" + durability : ""));
        FunnyFormatter formatter = new FunnyFormatter().register("_", "").register("{HASH}", "#");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return itemString.toString();
        }

        if (meta.hasDisplayName()) {
            itemString.append(" name:").append(formatter.format(ChatUtils.decolor(meta.getDisplayName())));
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore().stream().map(ChatUtils::decolor).map(formatter::format).collect(Collectors.toList());
            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = meta.getEnchants().entrySet().stream()
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase() + ":" + entry.getValue())
                    .collect(Collectors.toList());

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = meta.getItemFlags().stream().map(ItemFlag::name).map(String::toLowerCase).collect(Collectors.toList());
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

        if (EggTypeChanger.needsSpawnEggMeta()) {
            if (meta instanceof SpawnEggMeta) {
                SpawnEggMeta eggMeta = (SpawnEggMeta) meta;
                String entityType = eggMeta.getSpawnedType().name().toLowerCase();
                itemString.append(" eggtype:").append(entityType);
            }
        }

        return itemString.toString();
    }

    private static Enchantment matchEnchant(String enchantName) {
        if (BY_IN_GAME_NAME_ENCHANT != null && CREATE_NAMESPACED_KEY != null) {
            try {
                Object namespacedKey = CREATE_NAMESPACED_KEY.invoke(null, enchantName.toLowerCase());
                Object enchantment = BY_IN_GAME_NAME_ENCHANT.invoke(null, namespacedKey);

                if (enchantment != null) {
                    return (Enchantment) enchantment;
                }
            }
            catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        return Enchantment.getByName(enchantName.toUpperCase());
    }

    private static String getEnchantName(Enchantment enchantment) {
        if (GET_IN_GAME_NAME_ENCHANT != null && GET_NAMESPACED_KEY != null) {
            try {
                Object enchantmentName = GET_IN_GAME_NAME_ENCHANT.invoke(enchantment);
                Object namespacedKey = GET_NAMESPACED_KEY.invoke(enchantmentName);

                if (namespacedKey != null) {
                    return (String) namespacedKey;
                }
            }
            catch (InvocationTargetException | IllegalAccessException ignored) {
            }
        }

        return enchantment.getName();
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");

        Enchantment enchant = matchEnchant(split[0]);
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }

        int level;
        try {
            level = Integer.parseInt(split[1]);
        }
        catch (NumberFormatException numberFormatException) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
            level = 1;
        }

        return Pair.of(enchant, level);
    }

    private static Option<ItemFlag> matchItemFlag(String flagName) {
        try {
            return Option.of(ItemFlag.valueOf(flagName.toUpperCase()));
        }
        catch (IllegalArgumentException exception) {
            return Option.none();
        }
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        int amount = 0;

        for (ItemStack is : inv.getContents()) {
            if (item.isSimilar(is)) {
                amount += is.getAmount();
            }
        }

        return amount;
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

}
