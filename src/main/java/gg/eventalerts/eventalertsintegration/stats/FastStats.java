package gg.eventalerts.eventalertsintegration.stats;

import dev.faststats.Metrics;
import dev.faststats.data.Metric;
import eu.okaeri.configs.OkaeriConfig;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.stats.loader.FastStatsLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
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

    @Override @NotNull
    public Map<String, Supplier<OkaeriConfig>> getConfigs() {
        return Map.of("config", () -> plugin.config);
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
                .addMetric(enumArray("config_event_messages_ignored_types", () -> plugin.config.event_messages.ignored_types))
                .addMetric(enumArray("config_event_messages_ignored_partner_roles", () -> plugin.config.event_messages.ignored_partner_roles))
                .addMetric(enumArray("config_event_messages_ignored_formats", () -> plugin.config.event_messages.ignored_formats))
                .addMetric(stringArray("config_event_messages_host_filter", () -> plugin.config.event_messages.host_filter));
    }
}
