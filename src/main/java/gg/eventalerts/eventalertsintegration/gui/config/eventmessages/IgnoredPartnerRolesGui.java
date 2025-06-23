package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.container.type.GuiContainerType;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;

import gg.eventalerts.eventalertsintegration.config.PingRole;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class IgnoredPartnerRolesGui extends EventMessagesGui {
    public IgnoredPartnerRolesGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final int rows = (PingRole.PARTNER_PINGABLE.size() + GuiContainerType.COLUMNS - 1) / GuiContainerType.COLUMNS; // Add (GuiContainerType.COLUMNS - 1) to round up instead of down
        final PaperGuiBuilder builder = Gui.of(rows);

        // Add ping role buttons
        int i = 0;
        for (final PingRole role : PingRole.PARTNER_PINGABLE) {
            final int finalI = i;
            builder.statelessComponent(container1 -> container1.setItem(finalI, booleanItem(
                    plugin.config.eventMessages.ignoredPartnerRoles.contains(role),
                    role.name,
                    "Whether to ignore " + role.name + "\nevents for event messages",
                    (player, context) -> {
                        playDingSound(plugin.config.eventMessages.toggleIgnoredPartnerRole(role));
                        open(false);
                    })));
            i++;
        }

        return builder
                .title(Component.text("Ignored Partner Roles"))
                .statelessComponent(container -> container.setItem((rows * GuiContainerType.COLUMNS) - 1, backButton()));
    }
}
