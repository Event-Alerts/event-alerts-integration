package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.container.type.GuiContainerType;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import gg.eventalerts.eventalertsintegration.config.EventFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


public class IgnoredFormatsGui extends EventMessagesGui {
    public IgnoredFormatsGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final EventFormat[] formats = EventFormat.values();
        final int rows = (formats.length + GuiContainerType.COLUMNS - 1) / GuiContainerType.COLUMNS; // Add (GuiContainerType.COLUMNS - 1) to round up instead of down
        final PaperGuiBuilder builder = Gui.of(rows);

        // Add format buttons
        int i = 0;
        for (final EventFormat format : formats) {
            final int finalI = i++;
            builder.statelessComponent(container1 -> container1.setItem(finalI, booleanItem(
                    plugin.config.event_messages.ignored_formats.contains(format),
                    format.name(),
                    "Whether to ignore " + format.name() + "\nevents for event messages",
                    (player, context) -> {
                        playDingSound(plugin.config.event_messages.toggleIgnoredFormat(format));
                        open(false);
                    })));
        }

        return builder
                .title(Component.text("Ignored Formats"))
                .statelessComponent(container -> container.setItem((rows * GuiContainerType.COLUMNS) - 1, backButton()));
    }
}
