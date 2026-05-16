package gg.eventalerts.eventalertsintegration.gui.config;

import dev.triumphteam.gui.click.action.SimpleGuiClickAction;
import dev.triumphteam.gui.element.GuiItem;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.EAGui;
import gg.eventalerts.eventalertsintegration.gui.config.advanced.AdvancedGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.EventMessagesGui;
import gg.eventalerts.eventalertsintegration.gui.config.syncing.SyncingGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ConfigGui extends EAGui {
    @NotNull private static final Component ENABLED = Component.text("Enabled", NamedTextColor.GREEN);
    @NotNull private static final Component DISABLED = Component.text("Disabled", NamedTextColor.RED);
    @NotNull public static final TextComponent.Builder CANCEL = Component.text()
            .color(NamedTextColor.RED)
            .decorate(TextDecoration.ITALIC)
            .append(Component.text("\nType "))
            .append(Component.text("cancel", NamedTextColor.DARK_RED))
            .append(Component.text(" to cancel the process\n"));

    public ConfigGui(@NotNull EventAlertsIntegration plugin, @NotNull Player opener) {
        super(plugin, opener);
    }

    public ConfigGui(@NotNull EAGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("EVENT ALERTS INTEGRATION"))
                .statelessComponent(container -> container.setItem(0, ItemBuilder.from(Material.REPEATER)
                        .name(unitalicize(Component.text("LINKING", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings related to Event Alerts'\nMinecraft-Discord linking system"))
                        .asGuiItem((player, context) -> new LinkingGui(this).open(true))))
                .statelessComponent(container -> container.setItem(1, ItemBuilder.from(Material.GOLDEN_SWORD)
                        .name(unitalicize(Component.text("CROSS-BAN", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings related to Event Alerts'\ncross-banning feature"))
                        .asGuiItem((player, context) -> new CrossBanGui(this).open(true))))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.from(Material.BELL)
                        .name(unitalicize(Component.text("EVENT MESSAGES", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings related to Event Alerts'\nevent messages being broadcast in-game"))
                        .asGuiItem((player, context) -> new EventMessagesGui(this).open(true))))
                .statelessComponent(container -> container.setItem(3, ItemBuilder.from(Material.ENDER_PEARL)
                        .name(unitalicize(Component.text("SYNCING", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Settings related to syncing\nbetween Discord and Minecraft"))
                        .asGuiItem((player, context) -> new SyncingGui(this).open(true))))
                .statelessComponent(container -> container.setItem(8, ItemBuilder.from(Material.REDSTONE_TORCH)
                        .name(unitalicize(Component.text("ADVANCED", NamedTextColor.DARK_RED, TextDecoration.BOLD)))
                        .lore(lore("<red>Advanced settings that you\n<red>probably shouldn't touch"))
                        .asGuiItem((player, context) -> new AdvancedGui(this).open(true))));
    }

    @NotNull
    public static GuiItem<Player, ItemStack> booleanItem(boolean value, @NotNull String title, @NotNull String description, @NotNull SimpleGuiClickAction<Player> action) {
        // Get lore
        final List<Component> lore = lore(description);
        lore.add(Component.empty());
        lore.add(unitalicize(Component.text()
                .append(Component.text("Current status: ", NamedTextColor.GRAY))
                .append(value ? ENABLED : DISABLED)
                .build()));

        // Return item
        return ItemBuilder.from(value ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                .name(unitalicize(EventAlertsIntegration.MINI_MESSAGE.deserialize(title).color(NamedTextColor.GOLD)))
                .lore(lore)
                .asGuiItem(action);
    }
}
