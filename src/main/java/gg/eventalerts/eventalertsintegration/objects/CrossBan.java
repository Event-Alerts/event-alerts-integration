package gg.eventalerts.eventalertsintegration.objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.util.Date;
import java.util.UUID;


public class CrossBan extends EAObject {
    // Database + Websocket
    @Nullable public UUID minecraftUuid;
    @Nullable public String reason;
    @Nullable public Date expiration;
    // Websocket
    @Nullable public Status status;

    @NotNull
    public TextComponent getReasonExpires() {
        final TextComponent.Builder builder = Component.text();
        if (reason != null) builder
                .append(Component.text("\n\nReason: ")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(reason)
                        .color(NamedTextColor.GRAY));
        if (expiration != null) builder
                .append(Component.text("\n\nExpires in: ")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(DurationFormatter.formatDuration(expiration.getTime() - System.currentTimeMillis(), "d'd' H'h' m'm' s's'"))
                        .color(NamedTextColor.GRAY));
        return builder.build();
    }

    public enum Status {
        ADDED,
        REMOVED,
        EDITED
    }
}
