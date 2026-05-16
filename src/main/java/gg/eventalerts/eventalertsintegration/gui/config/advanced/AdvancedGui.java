package gg.eventalerts.eventalertsintegration.gui.config.advanced;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.paper.container.type.HopperContainerType;

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
        return Gui.of(new HopperContainerType())
                .title(Component.text("Advanced", NamedTextColor.DARK_RED))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.advanced.useTestingApi,
                        "Use Testing API",
                        "Whether to enable using the testing API hosts\nOnly the developer really needs to enable this",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.advanced.useTestingApi;
                            plugin.config.advanced.setUseTestingApi(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.from(Material.REDSTONE)
                        .name(unitalicize(Component.text("WEBSOCKETS", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings for websocket connections"))
                        .asGuiItem((player, context) -> new WebsocketsGui(this).open(true))))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
