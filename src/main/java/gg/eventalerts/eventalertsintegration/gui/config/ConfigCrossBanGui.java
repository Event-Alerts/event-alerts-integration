package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.HopperContainerType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class ConfigCrossBanGui extends ConfigMainGui {
    public ConfigCrossBanGui(@NotNull ConfigMainGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGUI() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("EVENT ALERTS - Cross-ban", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .statelessComponent(container -> container.setItem(0,
                        ConfigMainGui.booleanItem(
                                plugin.config.crossBan.enabled,
                                Component.text("Enabled"),
                                "Whether to enable\ncross-ban checking",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.crossBan.enabled;
                                    plugin.config.crossBan.setEnabled(newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1,
                        ConfigMainGui.booleanItem(
                                plugin.config.crossBan.allowJoinOnFailure,
                                Component.text("Allow join on failure"),
                                "Whether to allow players to join the\nserver when the cross-ban check fails",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.crossBan.allowJoinOnFailure;
                                    plugin.config.crossBan.allowJoinOnFailure = newStatus;
                                    plugin.config.setSave(ConfigYml.CrossBan.PATH_ALLOW_JOIN_ON_FAILURE, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
