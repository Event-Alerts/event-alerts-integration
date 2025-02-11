package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.HopperContainerType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class ConfigLinkingGui extends ConfigMainGui {
    public ConfigLinkingGui(@NotNull ConfigMainGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGUI() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("EVENT ALERTS - Linking", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .statelessComponent(container -> container.setItem(0,
                        ConfigMainGui.booleanItem(
                                plugin.config.linking.requireLink,
                                "Require linking",
                                "Whether to force players to be linked\nwith Event Alerts to join the server",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.linking.requireLink;
                                    plugin.config.linking.setRequireLink(newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1,
                        ConfigMainGui.booleanItem(
                                plugin.config.linking.checkOnJoin,
                                "Check on join",
                                "Whether to check link status\nwhen a player joins the server",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.linking.checkOnJoin;
                                    plugin.config.linking.checkOnJoin = newStatus;
                                    plugin.config.setSave(ConfigYml.Linking.PATH_CHECK_ON_JOIN, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(2,
                        ConfigMainGui.booleanItem(
                                plugin.config.linking.allowJoinOnFailure,
                                "Allow join on failure",
                                "Whether to allow players to join the\nserver when the linking check fails",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.linking.allowJoinOnFailure;
                                    plugin.config.linking.allowJoinOnFailure = newStatus;
                                    plugin.config.setSave(ConfigYml.Linking.PATH_ALLOW_JOIN_ON_FAILURE, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
