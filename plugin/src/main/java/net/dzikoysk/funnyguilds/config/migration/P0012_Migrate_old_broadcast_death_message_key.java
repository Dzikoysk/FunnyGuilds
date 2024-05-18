package net.dzikoysk.funnyguilds.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;

public class P0012_Migrate_old_broadcast_death_message_key extends NamedMigration {

        public P0012_Migrate_old_broadcast_death_message_key() {
            super(
                    "Migrate old boolean broadcast-death-message key to new enum death-message-receivers",
                    move("broadcast-death-message", "death-message-receivers", oldValue -> {
                        if (oldValue instanceof Boolean && !((Boolean) oldValue)) {
                            return PluginConfiguration.DeathMessageReceivers.PARTICIPANTS;
                        }
                        return PluginConfiguration.DeathMessageReceivers.ALL;
                    })
            );
        }

}
