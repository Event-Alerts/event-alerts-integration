package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class PlayerConnection extends EAObject {
    @NotNull public final String uuid;
    @NotNull public final String username;
    @NotNull public final String timestamp;
    @NotNull public final Type type;
    
    public PlayerConnection(@NotNull UUID uuid, @NotNull String username, long timestamp, @NotNull Type type) {
        this.uuid = uuid.toString();
        this.username = username;
        this.timestamp = String.valueOf(timestamp);
        this.type = type;
    }

    public PlayerConnection(@NotNull JsonObject json) {
        this(
                UUID.fromString(json.get("uuid").getAsString()),
                json.get("username").getAsString(),
                json.get("timestamp").getAsLong(),
                Type.valueOf(json.get("type").getAsString().toUpperCase()));
    }

    public enum Type {
        JOIN,
        QUIT
    }
}
