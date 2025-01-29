package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.click.action.RunnableGuiClickAction;
import dev.triumphteam.gui.item.GuiItem;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.EAGUI;
import gg.eventalerts.eventalertsintegration.gui.HopperContainerType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ConfigMainGui extends EAGUI {
    @NotNull public static final String RED_BACK_ARROW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=";
    @NotNull public static final Component ENABLED = Component.text("Enabled", NamedTextColor.GREEN);
    @NotNull public static final Component DISABLED = Component.text("Disabled", NamedTextColor.RED);

    public ConfigMainGui(@NotNull EventAlertsIntegration plugin, @NotNull Player opener) {
        super(plugin, opener);
    }

    public ConfigMainGui(@NotNull EAGUI parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGUI() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("EVENT ALERTS INTEGRATION", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .statelessComponent(container -> container.setItem(0, ItemBuilder.from(Material.REPEATER)
                        .name(Component.text("LINKING", NamedTextColor.GOLD, TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false))
                        .lore(
                                Component.text("Settings related to Event Alerts'", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Minecraft-Discord linking system", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false))
                        .asGuiItem((player, context) -> new ConfigLinkingGui(this).open(true))))
                .statelessComponent(container -> container.setItem(1, ItemBuilder.from(Material.GOLDEN_SWORD)
                        .name(Component.text("CROSS-BAN", NamedTextColor.GOLD, TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false))
                        .lore(
                                Component.text("Settings related to Event Alerts'", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("cross-banning feature", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false))
                        .asGuiItem((player, context) -> new ConfigCrossBanGui(this).open(true))))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.from(Material.BELL)
                        .name(Component.text("EVENT MESSAGES", NamedTextColor.GOLD, TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false))
                        .lore(
                                Component.text("Settings related to Event Alerts'", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("event messages being broadcast in-game", NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false))
                        .asGuiItem((player, context) -> new ConfigEventMessagesGui(this).open(true))))
                .statelessComponent(container -> container.setItem(4, ItemBuilder.from(Material.REDSTONE_TORCH)
                        .name(Component.text("ADVANCED", NamedTextColor.DARK_RED, TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false))
                        .lore(
                                Component.text("Advanced settings that you probably", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("shouldn't touch", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false))
                        .asGuiItem((player, context) -> new ConfigAdvancedGui(this).open(true))));
    }

    @NotNull
    public static GuiItem<Player, ItemStack> booleanItem(boolean value, @NotNull Component title, @NotNull String description, @NotNull RunnableGuiClickAction<Player> action) {
        // Get lore
        final List<Component> lore = new ArrayList<>();
        for (final String line : description.split("\n")) lore.add(Component.text(line)
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.YELLOW));
        lore.add(Component.empty());
        lore.add(Component.text()
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Current status: ", NamedTextColor.GRAY))
                .append(value ? ENABLED : DISABLED)
                .build());

        // Return item
        return ItemBuilder.from(value ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                .name(title
                        .decoration(TextDecoration.ITALIC, false)
                        .color(NamedTextColor.GOLD))
                .lore(lore)
                .asGuiItem(action);
    }
}
