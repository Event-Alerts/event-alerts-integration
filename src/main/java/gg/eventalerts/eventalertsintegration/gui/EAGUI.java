package gg.eventalerts.eventalertsintegration.gui;

import dev.triumphteam.gui.item.GuiItem;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigMainGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class EAGUI {
    @NotNull public final EventAlertsIntegration plugin;
    @NotNull public final Player opener;
    @Nullable public final EAGUI parent;

    public EAGUI(@NotNull EventAlertsIntegration plugin, @NotNull Player opener, @Nullable EAGUI parent) {
        this.plugin = plugin;
        this.opener = opener;
        this.parent = parent;
    }

    public EAGUI(@NotNull EventAlertsIntegration plugin, @NotNull Player opener) {
        this(plugin, opener, null);
    }

    public EAGUI(@NotNull EAGUI parent) {
        this(parent.plugin, parent.opener, parent);
    }

    public void open(boolean sound) {
        if (sound) playPageSound();
        getGUI().build().open(opener);
    }

    public void back(boolean sound) {
        if (parent == null) throw new UnsupportedOperationException("Cannot go back from the main GUI");
        if (sound) playPageSound();
        parent.open(true);
    }

    @NotNull
    public abstract PaperGuiBuilder getGUI();

    public void playPageSound() {
        opener.playSound(opener.getLocation(), "ui.loom.select_pattern", 1, 1);
    }

    public void playDingSound(boolean newStatus) {
        opener.playSound(opener.getLocation(), "block.note_block.pling", 1, newStatus ? 2 : 0);
    }

    @NotNull
    public GuiItem<Player, ItemStack> backButton() {
        return ItemBuilder.skull()
                .texture(ConfigMainGui.RED_BACK_ARROW)
                .name(Component.text("← Back", NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false))
                .lore(
                        Component.text("Return to the", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("previous page", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false))
                .asGuiItem((player, context) -> back(true));
    }
}
