package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.SafeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Reflections {
    private static final Map<String, Class<?>> classCache = new HashMap<>();
    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final Map<String, FieldAccessor<?>> fieldAccessorCache = new HashMap<>();
    private static final Map<String, Method> methodCache = new HashMap<>();
    private static final Class<?> INVALID_CLASS = InvalidMarker.class;
    private static final Method INVALID_METHOD = SafeUtils.safeInit(() -> InvalidMarker.class.getDeclaredMethod("invalidMethodMaker"));
    private static final Field INVALID_FIELD = SafeUtils.safeInit(() -> InvalidMarker.class.getDeclaredField("invalidFieldMarker"));
    private static final FieldAccessor<?> INVALID_FIELD_ACCESSOR = getField(INVALID_CLASS, Void.class, 0);

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    public static String getFixedVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static Class<?> getClassOmitCache(String className) {
        classCache.remove(className);
        return getClass(className);
    }

    public static Class<?> getClass(String className) {
        Class<?> c = classCache.get(className);

        if (c != null) {
            return c != INVALID_CLASS ? c : null;
        }

        try {
            c = Class.forName(className);
            classCache.put(className, c);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
            classCache.put(className, INVALID_CLASS);
        }
        return c;
    }

    public static Class<?> getCraftClass(String name) {
        return getClass("net.minecraft.server." + getVersion() + name);
    }

    public static Class<?> getBukkitClass(String name) {
        return getClass("org.bukkit.craftbukkit." + getVersion() + name);
    }

    public static Object getHandle(Entity entity) {
        try {
            return getMethod(entity.getClass(), "getHandle").invoke(entity);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static Object getHandle(World world) {
        try {
            return getMethod(world.getClass(), "getHandle").invoke(world);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static String constructFieldCacheKey(Class<?> cl, String fieldName) {
        return cl.getName() + "." + fieldName;
    }

    public static Field getField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field field = fieldCache.get(cacheKey);

        if (field != null) {
            return field != INVALID_FIELD ? field : null;
        }

        try {
            field = cl.getDeclaredField(fieldName);
            fieldCache.put(cacheKey, field);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
            fieldCache.put(cacheKey, INVALID_FIELD);
        }

        return field;
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        final String cacheKey = target.getName() + "." + (name != null ? name : "NONE") + "." + fieldType.getName() + "." + index;

        FieldAccessor<T> output = (FieldAccessor<T>) fieldAccessorCache.get(cacheKey);

        if (output != null) {
            if (output == INVALID_FIELD_ACCESSOR) {
                throw new IllegalArgumentException("Cannot find field with type " + fieldType);
            }

            return output;
        }

        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                output = new FieldAccessor<T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T get(Object target) {
                        try {
                            return (T) field.get(target);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
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

        fieldAccessorCache.put(cacheKey, output != null ? output : INVALID_FIELD_ACCESSOR);

        if (output == null) {
            throw new IllegalArgumentException("Cannot find field with type " + fieldType);
        }

        return output;
    }

    public static Field getPrivateField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field c = fieldCache.get(cacheKey);

        if (c != null) {
            return c != INVALID_FIELD ? c : null;
        }

        try {
            c = cl.getDeclaredField(fieldName);
            c.setAccessible(true);
            fieldCache.put(cacheKey, c);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
            fieldCache.put(cacheKey, INVALID_FIELD);
        }

        return c;
    }

    public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
        String cacheKey = cl.getName() + "." + method + "." + (args == null ? "NONE" : Arrays.toString(args));

        Method output = methodCache.get(cacheKey);

        if (output != null) {
            return output != INVALID_METHOD ? output : null;
        }

        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method) && (args == null || classListEqual(args, m.getParameterTypes()))) {
                output = m;
                break;
            }
        }

        methodCache.put(cacheKey, output == null ? INVALID_METHOD : output);

        return output;
    }

    public static Method getMethod(Class<?> cl, String method) {
        return getMethod(cl, method, null);
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