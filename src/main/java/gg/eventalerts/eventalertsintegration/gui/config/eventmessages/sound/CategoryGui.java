package gg.eventalerts.eventalertsintegration.gui.config.eventmessages.sound;

import dev.triumphteam.gui.container.type.GuiContainerType;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;

import net.kyori.adventure.text.Component;

import org.bukkit.SoundCategory;

import org.jetbrains.annotations.NotNull;


public class CategoryGui extends SoundGui {
    public CategoryGui(@NotNull SoundGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        // Get rows
        final SoundCategory[] categories = SoundCategory.values();
        final int rows = (categories.length + GuiContainerType.COLUMNS - 1) / GuiContainerType.COLUMNS; // Add (GuiContainerType.COLUMNS - 1) to round up instead of down
        final PaperGuiBuilder builder = Gui.of(rows);

        // Add sound category buttons
        final SoundCategory current = plugin.config.eventMessages.sound != null ? (SoundCategory) plugin.config.eventMessages.sound.category : null;
        int i = 0;
        for (final SoundCategory category : categories) {
            final int finalI = i;
            builder.statelessComponent(container -> container.setItem(finalI, booleanItem(
                    current == category,
                    category.name(),
                    "Whether to play the sound\nin the " + category.name() + " category",
                    (player, context) -> {
                        plugin.config.eventMessages.sound.category = category;
                        plugin.config.setSave(ConfigYml.EventMessages.PATH_SOUND + ".category", category.name());
                        playDingSound(true);
                        open(false);
                    })));
            i++;
        }

        return builder
                .title(Component.text("Sound Category"))
                .statelessComponent(container -> container.setItem((rows * GuiContainerType.COLUMNS) - 1, backButton()));
    }
}
