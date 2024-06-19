package net.dzikoysk.funnyguilds.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.SafeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public final class Reflections {

    public static boolean USE_PRE_13_METHODS;
    public static boolean USE_PRE_12_METHODS;
    public static boolean USE_PRE_9_METHODS;

    private static String CRAFTBUKKIT_PACKAGE;
    private static final String NMS_PACKAGE = "net.minecraft";
    private static String NMS_WITH_VERSION_PACKAGE = null;

    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();
    private static final Map<String, Field> FIELD_CACHE = new HashMap<>();
    private static final Map<String, FieldAccessor<?>> FIELD_ACCESSOR_CACHE = new HashMap<>();
    private static final Map<String, Method> METHOD_CACHE = new HashMap<>();
    private static final Class<?> INVALID_CLASS = InvalidMarker.class;
    private static final Method INVALID_METHOD = SafeUtils.safeInit(() -> InvalidMarker.class.getDeclaredMethod("invalidMethodMaker"));
    private static final Field INVALID_FIELD = SafeUtils.safeInit(() -> InvalidMarker.class.getDeclaredField("invalidFieldMarker"));
    private static final FieldAccessor<?> INVALID_FIELD_ACCESSOR = getField(INVALID_CLASS, Void.class, 0);

    private Reflections() {
    }

    public static void prepareServerVersion() {
        Server server = Bukkit.getServer();

        int versionNumber = Integer.parseInt(server.getBukkitVersion().split("-")[0].split("\\.")[1]);

        USE_PRE_13_METHODS = versionNumber < 13;
        USE_PRE_12_METHODS = versionNumber < 12;
        USE_PRE_9_METHODS = versionNumber < 9;

        CRAFTBUKKIT_PACKAGE = server.getClass().getPackage().getName();
        if (versionNumber < 17) {
            NMS_WITH_VERSION_PACKAGE = NMS_PACKAGE + ".server." + CRAFTBUKKIT_PACKAGE.split("\\.")[3];
        }
    }

    public static Class<?> getClassOmitCache(String className) {
        CLASS_CACHE.remove(className);
        return getClass(className);
    }

    public static Class<?> getClass(String className) {
        Class<?> c = CLASS_CACHE.get(className);

        if (c != null) {
            return c != INVALID_CLASS ? c : null;
        }

        try {
            c = Class.forName(className);
            CLASS_CACHE.put(className, c);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve class", ex);

            CLASS_CACHE.put(className, INVALID_CLASS);
        }
        return c;
    }

    public static Class<?> getNMSClass(String name, String subPackage) {
        String nmsPackage = Objects.requireNonNull(NMS_WITH_VERSION_PACKAGE, () -> NMS_PACKAGE + "." + subPackage);
        return getClass(nmsPackage + "." + name);
    }

    public static Class<?> getCraftBukkitClass(String name) {
        return getClass(CRAFTBUKKIT_PACKAGE + "." + name);
    }

    public static Class<?> getBukkitClass(String name) {
        return getClass("org.bukkit." + name);
    }

    public static Object getHandle(Entity entity) {
        try {
            return getMethod(entity.getClass(), "getHandle").invoke(entity);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not get entity handle", ex);

            return null;
        }
    }

    public static Object getHandle(World world) {
        try {
            return getMethod(world.getClass(), "getHandle").invoke(world);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not get world handle", ex);

            return null;
        }
    }

    private static String constructFieldCacheKey(Class<?> cl, String fieldName) {
        return cl.getName() + "." + fieldName;
    }

    public static Field getField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field field = FIELD_CACHE.get(cacheKey);

        if (field != null) {
            return field != INVALID_FIELD ? field : null;
        }

        try {
            field = cl.getDeclaredField(fieldName);
            FIELD_CACHE.put(cacheKey, field);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve field", ex);

            FIELD_CACHE.put(cacheKey, INVALID_FIELD);
        }

        return field;
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    @SuppressWarnings("unchecked")
    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        String cacheKey = target.getName() + "." + (name != null ? name : "NONE") + "." + fieldType.getName() + "." + index;

        FieldAccessor<T> output = (FieldAccessor<T>) FIELD_ACCESSOR_CACHE.get(cacheKey);

        if (output != null) {
            if (output == INVALID_FIELD_ACCESSOR) {
                throw new IllegalArgumentException("Cannot find field with type " + fieldType);
            }

            return output;
        }

        for (Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                output = new FieldAccessor<T>() {

                    @Override
                    public T get(Object target) {
                        try {
                            return (T) field.get(target);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public boolean hasField(Object target) {
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };

                break;
            }
        }

        if (output == null && target.getSuperclass() != null) {
            output = getField(target.getSuperclass(), name, fieldType, index);
        }

        FIELD_ACCESSOR_CACHE.put(cacheKey, output != null ? output : INVALID_FIELD_ACCESSOR);

        if (output == null) {
            throw new IllegalArgumentException("Cannot find field with type " + fieldType);
        }

        return output;
    }

    public static Field getPrivateField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field c = FIELD_CACHE.get(cacheKey);
        if (c != null) {
            return c != INVALID_FIELD ? c : null;
        }

        try {
            c = cl.getDeclaredField(fieldName);
            c.setAccessible(true);
            FIELD_CACHE.put(cacheKey, c);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve field", ex);

            FIELD_CACHE.put(cacheKey, INVALID_FIELD);
        }

        return c;
    }

    public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
        String cacheKey = cl.getName() + "." + method + "." + (args == null ? "NONE" : Arrays.toString(args));

        Method output = METHOD_CACHE.get(cacheKey);
        if (output != null) {
            return output != INVALID_METHOD ? output : null;
        }

        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method) && (args == null || classListEqual(args, m.getParameterTypes()))) {
                output = m;
                break;
            }
        }

        METHOD_CACHE.put(cacheKey, output == null ? INVALID_METHOD : output);
        return output;
    }

    public static Method getMethod(Class<?> cl, String method) {
        return getMethod(cl, method, (Class<?>[]) null);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... arguments) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), arguments)) {
                return constructor;
            }
        }

        return null;
    }

    public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
        if (l1.length != l2.length) {
            return false;
        }

        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                return false;
            }
        }

        return true;
    }

    public interface ConstructorInvoker {
        Object invoke(Object... arguments);
    }

    public interface MethodInvoker {
        Object invoke(Object target, Object... arguments);
    }

    public interface FieldAccessor<T> {
        T get(Object target);

        void set(Object target, Object value);

        boolean hasField(Object target);
    }

    private static class InvalidMarker {
        public Void invalidFieldMarker;

        public void invalidMethodMaker() {
        }
    }

}
