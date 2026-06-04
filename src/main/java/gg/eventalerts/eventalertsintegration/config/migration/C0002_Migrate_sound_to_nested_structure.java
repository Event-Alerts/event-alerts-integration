package gg.eventalerts.eventalertsintegration.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.*;


public class C0002_Migrate_sound_to_nested_structure extends NamedMigration {
    public C0002_Migrate_sound_to_nested_structure() {
        super("migrates sound to nested structure inside sound key",
                when(
                        // Only migrate if sound not already nested
                        not(exists("event_messages.sound.sound.sound")),
                        multi(
                                move("event_messages.sound.sound", "event_messages.sound.sound.sound"),
                                move("event_messages.sound.category", "event_messages.sound.sound.category"),
                                move("event_messages.sound.volume", "event_messages.sound.sound.volume"),
                                move("event_messages.sound.pitch", "event_messages.sound.sound.pitch"))));
    }
}
