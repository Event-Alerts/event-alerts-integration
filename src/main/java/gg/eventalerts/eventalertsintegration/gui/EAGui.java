package gg.eventalerts.eventalertsintegration.gui;

import dev.triumphteam.gui.element.GuiItem;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public abstract class EAGui {

    @NotNull public final EventAlertsIntegration plugin;
    @NotNull public final Player opener;
    @Nullable public final EAGui parent;

    public EAGui(@NotNull EventAlertsIntegration plugin, @NotNull Player opener, @Nullable EAGui parent) {
        this.plugin = plugin;
        this.opener = opener;
        this.parent = parent;
    }

    public EAGui(@NotNull EventAlertsIntegration plugin, @NotNull Player opener) {
        this(plugin, opener, null);
    }

    public EAGui(@NotNull EAGui parent) {
        this(parent.plugin, parent.opener, parent);
    }

    public void open(boolean sound) {
        if (sound) playPageSound();
        getGui().build().open(opener);
    }

    public void back(boolean sound) {
        if (parent == null) throw new UnsupportedOperationException("Cannot go back from the main GUI");
        if (sound) playPageSound();
        parent.open(true);
    }

    @NotNull
    public abstract PaperGuiBuilder getGui();

    public void playPageSound() {
        opener.playSound(opener.getLocation(), "ui.loom.select_pattern", 1, 1);
    }

    public void playDingSound(boolean newStatus) {
        opener.playSound(opener.getLocation(), "block.note_block.pling", 1, newStatus ? 2 : 0);
    }

    @NotNull
    public GuiItem<Player, ItemStack> backButton() {
        return ItemBuilder.skull()
                .texture(Heads.RED_ARROW_LEFT)
                .name(unitalicize(Component.text("← Back", NamedTextColor.RED)))
                .lore(lore("<gray>Return to the\n<gray>previous page"))
                .asGuiItem((player, context) -> back(true));
    }

    @NotNull
    public static List<Component> lore(@NotNull String lore) {
        final List<Component> components = new ArrayList<>();
        for (String line : lore.split("\n")) {
            if (!line.startsWith("<")) line = "<yellow>" + line; // Default color
            components.add(unitalicize(EventAlertsIntegration.MINI_MESSAGE.deserialize(line)));
        }
        return components;
    }

    @NotNull
    public static Component unitalicize(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
