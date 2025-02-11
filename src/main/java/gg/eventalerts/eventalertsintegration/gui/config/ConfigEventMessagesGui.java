package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.EAGUI;
import gg.eventalerts.eventalertsintegration.gui.HopperContainerType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class ConfigEventMessagesGui extends ConfigMainGui {
    public ConfigEventMessagesGui(@NotNull EAGUI parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGUI() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("EVENT ALERTS - Events", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .statelessComponent(container -> container.setItem(0,
                        ConfigMainGui.booleanItem(
                                plugin.config.eventMessages.enabled,
                                "Enabled",
                                "Whether to enable event messages\nbeing broadcast in the server chat",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.eventMessages.enabled;
                                    plugin.config.eventMessages.setEnabled(newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1,
                        ConfigMainGui.booleanItem(
                                plugin.config.eventMessages.detectIps,
                                "Detect IPs <dark_gray>1.20.5+",
                                "If an IP is detected in an event message,\nplayers will be able to click a button to join\nthe event's server using transfer packets",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.eventMessages.detectIps;
                                    plugin.config.eventMessages.detectIps = newStatus;
                                    plugin.config.setSave(ConfigYml.EventMessages.PATH_DETECT_IPS, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(2,
                        ConfigMainGui.booleanItem(
                                plugin.config.eventMessages.soundEnabled,
                                "Sound",
                                "Whether to play a sound when\nan event message is broadcasted",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.eventMessages.soundEnabled;
                                    plugin.config.eventMessages.soundEnabled = newStatus;
                                    plugin.config.setSave(ConfigYml.EventMessages.PATH_SOUND_ENABLED, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
