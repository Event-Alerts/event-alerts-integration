package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.container.type.GuiContainerType;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import gg.eventalerts.eventalertsintegration.object.sdk.EventUtility;
import gg.eventalerts.sdk.object.EAEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


public class IgnoredPartnerRolesGui extends EventMessagesGui {
    public IgnoredPartnerRolesGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final int rows = (EventUtility.PingRole.PARTNER_PINGABLE.size() + GuiContainerType.COLUMNS - 1) / GuiContainerType.COLUMNS; // Add (GuiContainerType.COLUMNS - 1) to round up instead of down
        final PaperGuiBuilder builder = Gui.of(rows);

        // Add ping role buttons
        int i = 0;
        for (final EAEvent.PingRole role : EventUtility.PingRole.PARTNER_PINGABLE) {
            final int finalI = i++;
            builder.statelessComponent(container1 -> container1.setItem(finalI, booleanItem(
                    plugin.config.event_messages.ignored_partner_roles.contains(role),
                    role.displayName,
                    "Whether to ignore " + role.displayName + "\nevents for event messages",
                    (player, context) -> {
                        playDingSound(plugin.config.event_messages.toggleIgnoredPartnerRole(role));
                        open(false);
                    })));
        }

        return builder
                .title(Component.text("Ignored Partner Roles"))
                .statelessComponent(container -> container.setItem((rows * GuiContainerType.COLUMNS) - 1, backButton()));
    }
}
