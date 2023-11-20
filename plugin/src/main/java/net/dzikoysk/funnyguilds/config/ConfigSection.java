package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.exception.OkaeriException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;

public abstract class ConfigSection extends OkaeriConfig {

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();
        this.processPropertiesInternal();
        return this;
    }

    public void processProperties() {
    }

    private void processPropertiesInternal() {
        this.processProperties();

        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Exclude.class) != null) {
                continue;
            }

            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (ConfigSection.class.isAssignableFrom(fieldType)) {
                try {
                    ConfigSection section = (ConfigSection) field.get(this);
                    if (section == null) {
                        continue;
                    }
                    section.processPropertiesInternal();
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load properties for " + field.getName(), ex);
                }
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                try {
                    Collection<?> collection = (Collection<?>) field.get(this);
                    if (collection == null) {
                        continue;
                    }
                    collection.forEach((value) -> {
                        if (!(value instanceof ConfigSection section)) {
                            return;
                        }
                        section.processPropertiesInternal();
                    });
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load properties for " + field.getName(), ex);
                }
            } else if (Map.class.isAssignableFrom(fieldType)) {
                try {
                    Map<?, ?> map = (Map<?, ?>) field.get(this);
                    if (map == null) {
                        continue;
                    }
                    map.values().forEach((value) -> {
                        if (!(value instanceof ConfigSection section)) {
                            return;
                        }
                        section.processPropertiesInternal();
                    });
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load properties for " + field.getName(), ex);
                }
            }

            this.colorField(field);
        }
    }

    private void colorField(Field field) {
        try {
            if (field.getAnnotation(AutoColor.class) == null) {
                return;
            }

            Object newValue;
            Class<?> fieldType = field.getType();
            if (String.class.isAssignableFrom(fieldType)) {
                newValue = ChatUtils.colored((String) field.get(this));
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                Collection<?> collection = (Collection<?>) field.get(this);
                if (collection == null || collection.isEmpty()) {
                    return;
                }

                Collection<Object> newCollection;
                if (List.class.isAssignableFrom(fieldType)) {
                    newCollection = new ArrayList<>(collection);
                } else if (Set.class.isAssignableFrom(fieldType)) {
                    newCollection = new LinkedHashSet<>(collection);
                } else {
                    return;
                }

                collection.forEach(value -> {
                    if (value instanceof String string) {
                        newCollection.add(ChatUtils.colored(string));
                    } else {
                        newCollection.add(value);
                    }
                });

                newValue = newCollection;
            } else if (Map.class.isAssignableFrom(fieldType)) {
                Map<?, ?> map = (Map<?, ?>) field.get(this);
                if (map == null || map.isEmpty()) {
                    return;
                }

                Map<?, Object> newMap = new LinkedHashMap<>(map);
                newMap.replaceAll((key, value) -> {
                    if (value instanceof String string) {
                        return ChatUtils.colored(string);
                    }
                    return value;
                });
                newValue = newMap;
            } else {
                return;
            }
            field.set(this, newValue);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to color property " + field.getName(), ex);
        }
    }

}
