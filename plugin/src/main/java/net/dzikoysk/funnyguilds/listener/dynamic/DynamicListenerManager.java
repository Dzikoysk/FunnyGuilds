package net.dzikoysk.funnyguilds.listener.dynamic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.Injector;

public class DynamicListenerManager {

    private final Set<DynamicListenerRegistration> registrations = new HashSet<>();
    private final FunnyGuilds funnyGuilds;

    public DynamicListenerManager(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
    }

    public void registerDynamic(Supplier<Boolean> predicate, Listener... listeners) {
        DynamicListenerRegistration registration = new DynamicListenerRegistration(this.funnyGuilds, Arrays.asList(listeners), predicate);
        registration.reload();
        this.registrations.add(registration);
    }

    @SafeVarargs
    public final void registerDynamic(Supplier<Boolean> predicate, Injector injector, Class<? extends Listener>... listenerClasses) {
        Listener[] listeners = Arrays.stream(listenerClasses)
                .map(listenerClass -> injector.newInstanceWithFields(listenerClass))
                .toArray(Listener[]::new);
        this.registerDynamic(predicate, listeners);
    }

    public boolean unregister(Listener listener) {
        Iterator<DynamicListenerRegistration> registrationIterator = this.registrations.iterator();
        boolean result = false;

        while (registrationIterator.hasNext()) {
            DynamicListenerRegistration next = registrationIterator.next();

            if (!next.getListeners().remove(listener)) {
                continue;
            }

            result = true;

            if (next.getListeners().isEmpty()) {
                registrationIterator.remove();
            }
        }

        return result;
    }

    public void reloadAll() {
        this.registrations.forEach(DynamicListenerRegistration::reload);
    }

    public void unregisterAll() {
        this.registrations.forEach(DynamicListenerRegistration::forceUnregister);
        this.registrations.clear();
    }

}
