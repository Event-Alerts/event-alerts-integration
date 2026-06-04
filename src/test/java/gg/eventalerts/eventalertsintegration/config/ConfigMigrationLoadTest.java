package gg.eventalerts.eventalertsintegration.config;

import gg.eventalerts.eventalertsintegration.config.migration.C0001_Rename_hyphen_names_to_snake_case;
import gg.eventalerts.eventalertsintegration.config.migration.C0002_Migrate_sound_to_nested_structure;
import gg.eventalerts.eventalertsintegration.config.migration.C0003_Migrate_negative_retry_delay;
import gg.eventalerts.eventalertsintegration.config.migration.C0004_Migrate_websockets_to_websocket;
import gg.eventalerts.eventalertsintegration.config.serdes.PlayableSoundSerializer;
import gg.eventalerts.eventalertsintegration.config.validator.EAConfigValidator;
import eu.okaeri.configs.exception.ValidationException;
import eu.okaeri.configs.migrate.ConfigMigration;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConfigMigrationLoadTest {
    @TempDir(cleanup = CleanupMode.NEVER)
    Path tempDir;

    @Test
    void loadsCurrentDefaultsWithoutNullCollections() throws IOException {
        final ConfigYml config = loadConfig("""
                """);

        assertAll(
                () -> assertNotNull(config.syncing),
                () -> assertNotNull(config.linking),
                () -> assertNotNull(config.cross_ban),
                () -> assertNotNull(config.event_messages),
                () -> assertNotNull(config.advanced),
                () -> assertNotNull(config.event_messages.ignored_types),
                () -> assertNotNull(config.event_messages.ignored_partner_roles),
                () -> assertNotNull(config.event_messages.ignored_formats),
                () -> assertNotNull(config.event_messages.host_filter),
                () -> assertNotNull(config.event_messages.sound),
                () -> assertNotNull(config.event_messages.sound.sound));

        assertAll(
                () -> assertEquals(Set.of(EventType.SIGHTING), config.event_messages.ignored_types),
                () -> assertEquals(Set.of(PingRole.HOUSING, PingRole.CIVILIZATION), config.event_messages.ignored_partner_roles),
                () -> assertEquals(Set.of(), config.event_messages.ignored_formats),
                () -> assertEquals(Set.of(), config.event_messages.host_filter),
                () -> assertTrue(config.advanced.websocket.retry),
                () -> assertEquals(Duration.ofMinutes(5), config.advanced.websocket.retry_delay),
                () -> assertEquals(Sound.BLOCK_NOTE_BLOCK_CHIME, config.event_messages.sound.sound.sound),
                () -> assertEquals(SoundCategory.AMBIENT, config.event_messages.sound.sound.category));
    }

    @Test
    void migratesFullyLegacyConfigAndPreservesComplexNestedValues() throws IOException {
        final Path configFile = writeConfig("""
                api-keys:
                  player: EA.Player.1.0123456789abcdef01234567
                  server: EA.PartnerServer.1.89abcdef0123456701234567

                syncing:
                  discord-to-minecraft:
                    messages:
                      enabled: true
                      format: "<gray>[<event_type>] <event_title>"
                  minecraft-to-discord:
                    connections: false

                linking:
                  require-link: false
                  check-on-join: false
                  allow-join-on-failure: true

                cross-ban:
                  enabled: false
                  check-on-join: false
                  allow-join-on-failure: true

                event-messages:
                  enabled: true
                  detect-ips: false
                  sound:
                    enabled: true
                    sound: BLOCK_BELL
                    category: AMBIENT
                    volume: 0.5
                    pitch: 1.25
                  ignored-types:
                    - SIGHTING
                    - FAMOUS
                    - SIGHTING
                  ignored-partner-roles:
                    - MONEY
                    - HOUSING
                  ignored-formats:
                    - CUSTOM
                    - BUILT
                  host-filter:
                    - 670c8827e780b066783c9154
                    - '242385234992037888'
                    - '242385234992037888'

                advanced:
                  use-testing-api: true
                  websockets:
                    retry: true
                    retry-delay: -1
                    logs: true
                """);

        final ConfigYml config = loadConfig(configFile,
                new C0001_Rename_hyphen_names_to_snake_case(),
                new C0002_Migrate_sound_to_nested_structure(),
                new C0003_Migrate_negative_retry_delay(),
                new C0004_Migrate_websockets_to_websocket());

        assertAll(
                () -> assertEquals("EA.Player.1.0123456789abcdef01234567", config.api_keys.getPlayer()),
                () -> assertEquals("EA.PartnerServer.1.89abcdef0123456701234567", config.api_keys.getServer()),
                () -> assertTrue(config.syncing.discord_to_minecraft.messages.enabled),
                () -> assertEquals("<gray>[<event_type>] <event_title>", config.syncing.discord_to_minecraft.messages.format),
                () -> assertFalse(config.syncing.minecraft_to_discord.connections),
                () -> assertFalse(config.linking.check_on_join),
                () -> assertTrue(config.linking.allow_join_on_failure),
                () -> assertFalse(config.cross_ban.enabled),
                () -> assertFalse(config.cross_ban.check_on_join),
                () -> assertTrue(config.cross_ban.allow_join_on_failure),
                () -> assertTrue(config.event_messages.enabled),
                () -> assertFalse(config.event_messages.detect_ips),
                () -> assertEquals(Set.of(EventType.SIGHTING, EventType.FAMOUS), config.event_messages.ignored_types),
                () -> assertEquals(Set.of(PingRole.MONEY, PingRole.HOUSING), config.event_messages.ignored_partner_roles),
                () -> assertEquals(Set.of(EventFormat.CUSTOM, EventFormat.BUILT), config.event_messages.ignored_formats),
                () -> assertEquals(Set.of("670c8827e780b066783c9154", "242385234992037888"), config.event_messages.host_filter),
                () -> assertTrue(config.advanced.use_testing_api),
                () -> assertFalse(config.advanced.websocket.retry),
                () -> assertEquals(Duration.ofMinutes(5), config.advanced.websocket.retry_delay),
                () -> assertTrue(config.advanced.websocket.logs));

        final String migrated = Files.readString(configFile, StandardCharsets.UTF_8);
        assertAll(
                () -> assertFalse(migrated.contains("retry-delay: -1")),
                () -> assertTrue(migrated.contains("event_messages:")),
                () -> assertTrue(migrated.contains("cross_ban:")),
                () -> assertTrue(migrated.contains("websocket:")),
                () -> assertTrue(migrated.contains("host_filter:")));
    }

    @Test
    void migratesMixedStateConfigWithAlreadyRenamedParents() throws IOException {
        final Path configFile = writeConfig("""
                cross_ban:
                  enabled: true
                  check-on-join: false
                  allow-join-on-failure: true

                event_messages:
                  enabled: true
                  detect-ips: false
                  ignored-types:
                    - SIGHTING
                    - FAMOUS
                  ignored-partner-roles:
                    - MONEY
                  ignored-formats:
                    - CUSTOM
                  host-filter:
                    - 670c8827e780b066783c9154
                    - '242385234992037888'
                """);

        final ConfigYml config = loadConfig(configFile, new C0001_Rename_hyphen_names_to_snake_case());

        assertAll(
                () -> assertTrue(config.cross_ban.enabled),
                () -> assertFalse(config.cross_ban.check_on_join),
                () -> assertTrue(config.cross_ban.allow_join_on_failure),
                () -> assertTrue(config.event_messages.enabled),
                () -> assertFalse(config.event_messages.detect_ips),
                () -> assertEquals(Set.of(EventType.SIGHTING, EventType.FAMOUS), config.event_messages.ignored_types),
                () -> assertEquals(Set.of(PingRole.MONEY), config.event_messages.ignored_partner_roles),
                () -> assertEquals(Set.of(EventFormat.CUSTOM), config.event_messages.ignored_formats),
                () -> assertEquals(Set.of("670c8827e780b066783c9154", "242385234992037888"), config.event_messages.host_filter));

        final String migrated = Files.readString(configFile, StandardCharsets.UTF_8);
        assertAll(
                () -> assertFalse(migrated.contains("check-on-join")),
                () -> assertFalse(migrated.contains("allow-join-on-failure")),
                () -> assertFalse(migrated.contains("detect-ips")),
                () -> assertFalse(migrated.contains("ignored-types")),
                () -> assertFalse(migrated.contains("ignored-partner-roles")),
                () -> assertFalse(migrated.contains("ignored-formats")),
                () -> assertFalse(migrated.contains("host-filter")));
    }

    @Test
    void migratesLegacyNegativeRetryDelayToDisabledRetryAndDefaultDelay() throws IOException {
        final Path configFile = writeConfig("""
                advanced:
                  websockets:
                    retry: true
                    retry-delay: -1
                    logs: true
                """);

        final ConfigYml config = loadConfig(configFile,
                new C0001_Rename_hyphen_names_to_snake_case(),
                new C0003_Migrate_negative_retry_delay(),
                new C0004_Migrate_websockets_to_websocket());

        assertAll(
                () -> assertFalse(config.advanced.websocket.retry),
                () -> assertEquals(Duration.ofMinutes(5), config.advanced.websocket.retry_delay),
                () -> assertTrue(config.advanced.websocket.logs));

        final String migrated = Files.readString(configFile, StandardCharsets.UTF_8);
        assertAll(
                () -> assertFalse(migrated.contains("advanced.websockets")),
                () -> assertFalse(migrated.contains("retry-delay: -1")),
                () -> assertTrue(migrated.contains("advanced:")),
                () -> assertTrue(migrated.contains("websocket:")));
    }

    @Test
    void rejectsBelowMinimumRetryDelayDuringLoad() {
        final ConfigYml config = new ConfigYml(null);
        config.advanced.websocket.retry_delay = Duration.ofMinutes(2);

        assertThrows(ValidationException.class, () -> new EAConfigValidator().isValid(config.advanced.websocket));
    }

    @Test
    void loadIsIdempotentAfterMigration() throws IOException {
        final Path configFile = writeConfig("""
                event-messages:
                  enabled: true
                  detect-ips: false
                  ignored-types:
                    - SIGHTING
                  host-filter:
                    - 670c8827e780b066783c9154

                advanced:
                  websockets:
                    retry: true
                    retry-delay: -1
                """);

        final ConfigYml firstLoad = loadConfig(configFile,
                new C0001_Rename_hyphen_names_to_snake_case(),
                new C0002_Migrate_sound_to_nested_structure(),
                new C0003_Migrate_negative_retry_delay(),
                new C0004_Migrate_websockets_to_websocket());
        final String afterFirstLoad = Files.readString(configFile, StandardCharsets.UTF_8);

        final ConfigYml secondLoad = loadConfig(configFile);
        final String afterSecondLoad = Files.readString(configFile, StandardCharsets.UTF_8);

        assertAll(
                () -> assertEquals(firstLoad.event_messages.enabled, secondLoad.event_messages.enabled),
                () -> assertEquals(firstLoad.event_messages.detect_ips, secondLoad.event_messages.detect_ips),
                () -> assertEquals(firstLoad.event_messages.ignored_types, secondLoad.event_messages.ignored_types),
                () -> assertEquals(firstLoad.event_messages.host_filter, secondLoad.event_messages.host_filter),
                () -> assertEquals(firstLoad.advanced.websocket.retry, secondLoad.advanced.websocket.retry),
                () -> assertEquals(firstLoad.advanced.websocket.retry_delay, secondLoad.advanced.websocket.retry_delay),
                () -> assertEquals(afterFirstLoad, afterSecondLoad));
    }

    @NotNull
    private ConfigYml loadConfig(@NotNull String yaml, @NotNull ConfigMigration... migrations) throws IOException {
        final Path configFile = writeConfig(yaml);
        return loadConfig(configFile, migrations);
    }

    @NotNull
    private ConfigYml loadConfig(@NotNull Path configFile, @NotNull ConfigMigration... migrations) {
        return (ConfigYml) new ConfigYml(null)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer(), new SerdesCommons(), registry -> registry.register(new PlayableSoundSerializer()));
                    opt.validator(new EAConfigValidator());
                    opt.bindFile(configFile.toFile());
                    opt.removeOrphans(true);
                })
                .load()
                .migrate(migrations)
                .saveDefaults();
    }

    @NotNull
    private Path writeConfig(@NotNull String yaml) throws IOException {
        final Path configFile = tempDir.resolve("config.yml");
        Files.writeString(configFile, yaml.stripIndent().trim() + System.lineSeparator(), StandardCharsets.UTF_8);
        return configFile;
    }
}
