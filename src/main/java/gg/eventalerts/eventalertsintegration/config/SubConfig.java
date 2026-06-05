package gg.eventalerts.eventalertsintegration.config;

import eu.okaeri.configs.OkaeriConfig;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SubConfig extends OkaeriConfig {
    /**
     * Only {@code null} in unit tests
     */
    @Nullable public transient final EventAlertsIntegration plugin;

    public SubConfig(@Nullable EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public SubConfig save() {
        getContext().getRootConfig().save();
        return this;
    }
}
