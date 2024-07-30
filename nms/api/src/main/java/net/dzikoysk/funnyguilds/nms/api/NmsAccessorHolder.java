package net.dzikoysk.funnyguilds.nms.api;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;

import static java.lang.String.format;

public final class NmsAccessorHolder {

    private static final TreeMap<Integer, String> NMS_VERSION_MAPPING = new TreeMap<>(ImmutableMap.<Integer, String>builder()
            .put(3837, "v1_20R5") // Version can be found in server jar in version.json under `world_version` key
            .put(3953, "v1_21R1")
            .build());

    static final NmsAccessor INSTANCE = newAccessorInstance();

    private static NmsAccessor newAccessorInstance() {
        String nmsVersion = getNmsVersion();
        String className = format("net.dzikoysk.funnyguilds.nms.%s.%sNmsAccessor", nmsVersion, nmsVersion.toUpperCase());

        try {
            return (NmsAccessor) Class.forName(className).newInstance();
        }
        catch (Throwable th) {
            throw new RuntimeException(format("could not initialize NmsAccessor for version '%s'", nmsVersion), th);
        }
    }

    private static String getNmsVersion() {
        String version = null;

        try {
            Method getDataVersion = UnsafeValues.class.getMethod("getDataVersion");
            int dataVersion = (int) getDataVersion.invoke(Bukkit.getServer().getUnsafe());
            Map.Entry<Integer, String> versionEntry = NMS_VERSION_MAPPING.floorEntry(dataVersion);
            if (versionEntry != null) {
                version = versionEntry.getValue();
            }
        }
        catch (NoSuchMethodException ignored) {
            version = null;
        }
        catch(IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Could not get minecraft version", ex);
        }

        if (version != null) {
            return version;
        }

        // Fallback to legacy method
        StringBuilder nmsVersion = new StringBuilder(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);

        int revPosition = nmsVersion.lastIndexOf("_");
        nmsVersion.deleteCharAt(revPosition);

        return nmsVersion.toString();
    }

}
