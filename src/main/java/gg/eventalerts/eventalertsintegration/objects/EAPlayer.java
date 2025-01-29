package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class EAPlayer extends EAObject {
    @Nullable public final UUID uuid;
    @Nullable public final LinkStatus linkStatus;

    public EAPlayer(@NotNull JsonObject json) {
        super(json);
        this.uuid = json.has("uuid") ? UUID.fromString(json.get("uuid").getAsString()) : null;
        this.linkStatus = json.has("link_status") ? EventAlertsIntegration.getEnum(LinkStatus.class, json.get("link_status").getAsString()) : null;
    }

    public enum LinkStatus {
        ADDED,
        REMOVED
    }
}
