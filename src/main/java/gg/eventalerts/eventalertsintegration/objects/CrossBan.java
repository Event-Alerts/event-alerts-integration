package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.util.Date;
import java.util.UUID;


public class CrossBan extends EAObject {
    @NotNull private static final String PROP_MINECRAFT_UUID = "minecraftUuid";
    @NotNull private static final String PROP_REASON = "reason";
    @NotNull private static final String PROP_EXPIRATION = "expiration";
    @NotNull private static final String PROP_STATUS = "status";

    // Database + Websocket
    @NotNull public final UUID minecraftUuid;
    @NotNull public final String reason;
    @Nullable public final Date expiration;
    // Websocket
    @Nullable public final Status status;

    public CrossBan(@NotNull JsonObject json) {
        super(json);
        minecraftUuid = UUID.fromString(json.get(PROP_MINECRAFT_UUID).getAsString());
        reason = json.get(PROP_REASON).getAsString();
        expiration = MiscUtility.handleException(() -> new Date(json.get(PROP_EXPIRATION).getAsLong())).orElse(null);
        status = MiscUtility.handleException(() -> EventAlertsIntegration.getEnum(Status.class, json.get(PROP_STATUS).getAsString())).orElse(null);
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
