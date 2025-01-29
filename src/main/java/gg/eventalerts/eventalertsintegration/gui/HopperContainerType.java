package gg.eventalerts.eventalertsintegration.gui;

import dev.triumphteam.gui.exception.TriumphGuiException;
import dev.triumphteam.gui.paper.container.type.PaperContainerType;
import dev.triumphteam.gui.slot.Slot;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.NotNull;


/**
 * 1 row with 5 slots
 */
public class HopperContainerType implements PaperContainerType {
    @Override
    public int mapSlot(@NotNull Slot slot) {
        final int row = slot.row();
        final int column = slot.column();
        final int realSlot = (row * column) - 1;
        if (realSlot < 0 || realSlot > 5) throw new TriumphGuiException("Invalid slot (" + row + ", " + column + "). Valid range is (1, 1) to (1, 5).");
        return realSlot;
    }

    @Override @NotNull
    public Slot mapSlot(int slot) {
        return Slot.of(1, slot + 1);
    }

    @Override @NotNull
    public Inventory createInventory(@NotNull InventoryHolder holder, @NotNull Component title) {
        return Bukkit.createInventory(holder, InventoryType.HOPPER, title);
    }
}
