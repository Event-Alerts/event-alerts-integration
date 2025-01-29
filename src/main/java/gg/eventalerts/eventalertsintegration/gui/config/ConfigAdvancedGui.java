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


public class ConfigAdvancedGui extends ConfigMainGui {
    public ConfigAdvancedGui(@NotNull EAGUI parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGUI() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("EVENT ALERTS - Advanced", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                .statelessComponent(container -> container.setItem(0,
                        ConfigMainGui.booleanItem(
                                plugin.config.advanced.useTestingApi,
                                Component.text("Use Testing API"),
                                "Whether to enable using the testing API hosts\nOnly the developer really needs to enable this",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.advanced.useTestingApi;
                                    plugin.config.advanced.useTestingApi = newStatus;
                                    plugin.config.setSave(ConfigYml.Advanced.PATH_USE_TESTING_API, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1,
                        ConfigMainGui.booleanItem(
                                plugin.config.advanced.websockets.logs,
                                Component.text("WebSockets: Logs"),
                                "Whether to log websocket\nconnection messages",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.advanced.websockets.logs;
                                    plugin.config.advanced.websockets.logs = newStatus;
                                    plugin.config.setSave(ConfigYml.Advanced.Websockets.PATH_LOGS, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
