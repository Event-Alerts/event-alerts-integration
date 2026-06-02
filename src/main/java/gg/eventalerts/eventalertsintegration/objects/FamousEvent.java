package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.EventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class FamousEvent extends EAObject {
    @Nullable public final EventType type;
    @NotNull public final String message;

    public FamousEvent(@NotNull JsonObject json) {
        type = EventAlertsIntegration.GSON.fromJson(json.get("type"), EventType.class);
        message = json.get("message").getAsString();
    }
}
