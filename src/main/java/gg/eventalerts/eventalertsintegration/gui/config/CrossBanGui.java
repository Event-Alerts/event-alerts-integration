package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


public class CrossBanGui extends ConfigGui {
    public CrossBanGui(@NotNull ConfigGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("Cross-ban"))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.cross_ban.enabled,
                        "Enabled",
                        "Whether to enable\ncross-ban checking",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.cross_ban.enabled;
                            plugin.config.cross_ban.setEnabled(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(1, booleanItem(
                        plugin.config.cross_ban.check_on_join,
                        "Check on join",
                        "Whether to check cross-ban status\nwhen a player joins the server",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.cross_ban.check_on_join;
                            plugin.config.cross_ban.setCheckOnJoin(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(2, booleanItem(
                        plugin.config.cross_ban.allow_join_on_failure,
                        "Allow join on failure",
                        "Whether to allow players to join the\nserver when the cross-ban check fails",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.cross_ban.allow_join_on_failure;
                            plugin.config.cross_ban.setAllowJoinOnFailure(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
