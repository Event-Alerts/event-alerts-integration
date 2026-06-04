package gg.eventalerts.eventalertsintegration.gui.config.advanced;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import gg.eventalerts.eventalertsintegration.gui.EAGui;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;


public class AdvancedGui extends ConfigGui {
    public AdvancedGui(@NotNull EAGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("Advanced", NamedTextColor.DARK_RED))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.advanced.debug,
                        "Debug",
                        "Whether to enable debug logging",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.advanced.debug;
                            plugin.config.advanced.setDebug(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(1, booleanItem(
                        plugin.config.advanced.use_testing_api,
                        "Use Testing API",
                        "Whether to enable using the testing API hosts\nOnly the developer really needs to enable this",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.advanced.use_testing_api;
                            plugin.config.advanced.setUseTestingApi(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(3, ItemBuilder.from(Material.REDSTONE)
                        .name(unitalicize(Component.text("WEBSOCKET", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings for the\nwebsocket connection"))
                        .asGuiItem((player, context) -> new WebsocketGui(this).open(true))))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
