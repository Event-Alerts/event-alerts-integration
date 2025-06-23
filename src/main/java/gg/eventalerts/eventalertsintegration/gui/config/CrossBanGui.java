package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.container.type.HopperContainerType;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class CrossBanGui extends ConfigGui {
    public CrossBanGui(@NotNull ConfigGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("Cross-ban"))
                .statelessComponent(container -> container.setItem(0,
                        booleanItem(
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
                        booleanItem(
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
                        booleanItem(
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
