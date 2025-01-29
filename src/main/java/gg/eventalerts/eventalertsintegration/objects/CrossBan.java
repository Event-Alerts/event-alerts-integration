package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

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
    @NotNull public final UUID uuid;
    @NotNull public final String reason;
    @Nullable public final Date expiration;
    // Websocket
    @Nullable public final Status status;

    public CrossBan(@NotNull JsonObject json) {
        super(json);
        uuid = UUID.fromString(json.get("uuid").getAsString());
        reason = json.get("reason").getAsString();
        expiration = json.has("expiration") ? new Date(json.get("expiration").getAsLong()) : null;
        status = json.has("status") ? EventAlertsIntegration.getEnum(Status.class, json.get("status").getAsString()) : null;
    }

    @NotNull
    public TextComponent getReasonExpires() {
        final TextComponent.Builder builder = Component.text()
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
