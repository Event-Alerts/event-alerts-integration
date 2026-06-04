package gg.eventalerts.eventalertsintegration.config.migration;

import eu.okaeri.configs.migrate.builtin.NamedMigration;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.time.Duration;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.*;


public class C0003_Migrate_negative_retry_delay extends NamedMigration {
    public C0003_Migrate_negative_retry_delay() {
        super("sets 'advanced.websockets.retry' to false if 'advanced.websockets.retry_delay' is -1",
                when(
                        match("advanced.websockets.retry_delay", value -> {
                            // Returns true if value is negative
                            if (value instanceof Number number) return number.longValue() < 0;
                            if (value instanceof String string) return Mapper.toLong(string.trim())
                                    .map(num -> num < 0)
                                    .orElse(false);
                            return false;
                        }),
                        multi(
                                update("advanced.websockets.retry", oldValue -> false),
                                update("advanced.websockets.retry_delay", oldValue -> Duration.ofMinutes(5)))));
    }
}
