package net.dzikoysk.funnyguilds.listener.dynamic;

import java.util.Collection;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

class DynamicListenerRegistration {

    private final FunnyGuilds plugin;

    private final Collection<Listener> listeners;
    private final Supplier<Boolean> predicate;
    private boolean currentState;

    public DynamicListenerRegistration(FunnyGuilds plugin, Collection<Listener> listeners, Supplier<Boolean> predicate) {
        this.plugin = plugin;

        this.listeners = Validate.notNull(listeners, "listener");
        this.predicate = Validate.notNull(predicate, "predicate");
    }

    public void reload() {
        boolean requiredState = this.predicate.get();
        if (requiredState == this.currentState) {
            return;
        }

        if (requiredState) {
            this.forceRegister();
        }
        else {
            this.forceUnregister();
        }
    }

    public void forceRegister() {
        this.listeners.forEach(listener -> this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin));
        this.currentState = true;
    }

    public void forceUnregister() {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.currentState = false;
    }

    public Collection<Listener> getListeners() {
        return this.listeners;
    }

}
