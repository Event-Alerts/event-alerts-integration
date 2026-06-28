package gg.eventalerts.eventalertsintegration.stats;

import dev.faststats.Metrics;
import dev.faststats.data.Metric;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.stats.loader.FastStatsLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;


public class FastStats extends FastStatsLoader {
    @NotNull private final EventAlertsIntegration plugin;

    public FastStats(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getId() {
        return "ed6c97dcc635a7bf6dd4a9119f4c6788";
    }

    @Override
    public void mutateMetricsFactory(@NotNull Metrics.Factory factory) {
        // Load server.properties accepts-transfers
        Boolean serverPropertiesTransferBoolean = null;
        final Properties serverProperties = new Properties();
        final File serverPropertiesFile = new File("server.properties");
        if (serverPropertiesFile.exists()) {
            try {
                serverProperties.load(Files.newBufferedReader(serverPropertiesFile.toPath()));
                final String acceptsTransfers = serverProperties.getProperty("accepts-transfers");
                serverPropertiesTransferBoolean = acceptsTransfers != null ? Boolean.parseBoolean(acceptsTransfers) : null;
            } catch (final Exception e) {
                AnnoyingPlugin.log(Level.WARNING, "Failed to load server.properties", e);
            }
        }
        final Boolean finalServerPropertiesTransferBoolean = serverPropertiesTransferBoolean;

        // Mutate factory
        factory
                // FLUSH (reset cumulative metrics)
                .onFlush(() -> plugin.statsCollector.joinButtonClicks.set(0))

                // General
                .addMetric(Metric.bool("server_properties_accepts_transfer", () -> finalServerPropertiesTransferBoolean))
                .addMetric(Metric.number("join_button_clicks", plugin.statsCollector.joinButtonClicks::get))

                // Config
                .addMetric(Metric.bool("config_api_keys_player_has", () -> plugin.config.api_keys.getPlayer() != null))
                .addMetric(Metric.bool("config_api_keys_server_has", () -> plugin.config.api_keys.getServer() != null))
                .addMetric(Metric.bool("config_syncing_discord_to_minecraft_messages_enabled", () -> plugin.config.syncing.discord_to_minecraft.messages.enabled))
                .addMetric(Metric.bool("config_syncing_minecraft_to_discord_connections", () -> plugin.config.syncing.minecraft_to_discord.connections))
                .addMetric(Metric.bool("config_linking_require_link", () -> plugin.config.linking.require_link))
                .addMetric(Metric.bool("config_linking_check_on_join", () -> plugin.config.linking.check_on_join))
                .addMetric(Metric.bool("config_linking_allow_join_on_failure", () -> plugin.config.linking.allow_join_on_failure))
                .addMetric(Metric.bool("config_cross_ban_enabled", () -> plugin.config.cross_ban.enabled))
                .addMetric(Metric.bool("config_cross_ban_check_on_join", () -> plugin.config.cross_ban.check_on_join))
                .addMetric(Metric.bool("config_cross_ban_allow_join_on_failure", () -> plugin.config.cross_ban.allow_join_on_failure))
                .addMetric(Metric.bool("config_event_messages_enabled", () -> plugin.config.event_messages.enabled))
                .addMetric(Metric.bool("config_event_messages_detect_ips", () -> plugin.config.event_messages.detect_ips))
                .addMetric(Metric.bool("config_event_messages_sound_enabled", () -> plugin.config.event_messages.sound.enabled))
                .addMetric(Metric.string("config_event_messages_sound_sound_sound", () -> plugin.config.event_messages.sound.sound.sound.name()))
                .addMetric(Metric.string("config_event_messages_sound_sound_category", () -> plugin.config.event_messages.sound.sound.category.name()))
                .addMetric(Metric.number("config_event_messages_sound_sound_volume", () -> plugin.config.event_messages.sound.sound.volume))
                .addMetric(Metric.number("config_event_messages_sound_sound_pitch", () -> plugin.config.event_messages.sound.sound.pitch))
                .addMetric(Metric.stringArray("config_event_messages_ignored_types", () -> plugin.config.event_messages.ignored_types.stream()
                        .map(Enum::name)
                        .toArray(String[]::new)))
                .addMetric(Metric.stringArray("config_event_messages_ignored_partner_roles", () -> plugin.config.event_messages.ignored_partner_roles.stream()
                        .map(Enum::name)
                        .toArray(String[]::new)))
                .addMetric(Metric.stringArray("config_event_messages_ignored_formats", () -> plugin.config.event_messages.ignored_formats.stream()
                        .map(Enum::name)
                        .toArray(String[]::new)))
                .addMetric(Metric.stringArray("config_event_messages_host_filter", () -> plugin.config.event_messages.host_filter.toArray(String[]::new)))
                .addMetric(Metric.bool("config_advanced_debug", () -> plugin.config.advanced.debug))
                .addMetric(Metric.bool("config_advanced_use_testing_api", () -> plugin.config.advanced.use_testing_api))
                .addMetric(Metric.bool("config_advanced_websocket_retry", () -> plugin.config.advanced.websocket.retry))
                .addMetric(Metric.number("config_advanced_websocket_retry_delay", () -> plugin.config.advanced.websocket.retry_delay.toMillis()))
                .addMetric(Metric.bool("config_advanced_websocket_logs", () -> plugin.config.advanced.websocket.logs));
    }
}
