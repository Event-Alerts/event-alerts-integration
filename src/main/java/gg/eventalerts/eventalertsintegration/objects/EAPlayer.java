package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;

import java.util.UUID;


public class EAPlayer extends EAObject {
    @NotNull private static final String PROP_DISCORD = "discord";
    @NotNull private static final String PROP_MINECRAFT = "minecraft";
    @NotNull private static final String PROP_LINK_STATUS = "linkStatus";

    // Database + Websocket
    @Nullable public final Discord discord;
    @Nullable public final Minecraft minecraft;
    // Websocket
    @Nullable public final LinkStatus linkStatus;

    public EAPlayer(@NotNull JsonObject json) {
        super(json);
        this.discord = MiscUtility.handleException(() -> new Discord(json.getAsJsonObject(PROP_DISCORD))).orElse(null);
        this.minecraft = MiscUtility.handleException(() -> new Minecraft(json.getAsJsonObject(PROP_MINECRAFT))).orElse(null);
        this.linkStatus = MiscUtility.handleException(() -> EventAlertsIntegration.getEnum(LinkStatus.class, json.get(PROP_LINK_STATUS).getAsString())).orElse(null);
    }

    public static class Discord {
        @NotNull private static final String PROP_ID = "id";

        public final long id;

        public Discord(@NotNull JsonObject json) {
            this.id = json.get(PROP_ID).getAsLong();
        }
    }

    public static class Minecraft {
        @NotNull private static final String PROP_UUID = "uuid";

        @NotNull public final UUID uuid;

        public Minecraft(@NotNull JsonObject json) {
            this.uuid = UUID.fromString(json.get(PROP_UUID).getAsString());
        }
    }

    public enum LinkStatus {
        ADDED,
        REMOVED
    }
}
