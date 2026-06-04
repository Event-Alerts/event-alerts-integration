package gg.eventalerts.eventalertsintegration.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;


public class C0004_Migrate_websockets_to_websocket extends NamedMigration {
    public C0004_Migrate_websockets_to_websocket() {
        super("migrates the 'advanced.websockets' section to 'advanced.websocket'",
                move("advanced.websockets", "advanced.websocket"));
    }
}
