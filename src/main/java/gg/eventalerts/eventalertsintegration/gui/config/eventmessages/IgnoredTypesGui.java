package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.container.type.GuiContainerType;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.EventType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class IgnoredTypesGui extends EventMessagesGui {
    public IgnoredTypesGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final EventType[] types = EventType.values();
        final int rows = (types.length + GuiContainerType.COLUMNS - 1) / GuiContainerType.COLUMNS; // Add (GuiContainerType.COLUMNS - 1) to round up instead of down
        final PaperGuiBuilder builder = Gui.of(rows);

        // Add type buttons
        int i = 0;
        for (final EventType type : types) {
            final int finalI = i;
            builder.statelessComponent(container1 -> container1.setItem(finalI, booleanItem(
                    plugin.config.eventMessages.ignoredTypes.contains(type),
                    type.name,
                    "Whether to ignore " + type.name + "\nevents for event messages",
                    (player, context) -> {
                        playDingSound(plugin.config.eventMessages.toggleIgnoredType(type));
                        open(false);
                    })));
            i++;
        }

        return builder
                .title(Component.text("Ignored Types"))
                .statelessComponent(container -> container.setItem((rows * GuiContainerType.COLUMNS) - 1, backButton()));
    }
}
