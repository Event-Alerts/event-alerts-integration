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
                                "Enabled",
                                "Whether to enable\ncross-ban checking",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.crossBan.enabled;
                                    plugin.config.crossBan.setEnabled(newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1,
                        ConfigMainGui.booleanItem(
                                plugin.config.crossBan.checkOnJoin,
                                "Check on join",
                                "Whether to check cross-ban status\nwhen a player joins the server",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.crossBan.checkOnJoin;
                                    plugin.config.crossBan.checkOnJoin = newStatus;
                                    plugin.config.setSave(ConfigYml.CrossBan.PATH_CHECK_ON_JOIN, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(2,
                        ConfigMainGui.booleanItem(
                                plugin.config.crossBan.allowJoinOnFailure,
                                "Allow join on failure",
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
