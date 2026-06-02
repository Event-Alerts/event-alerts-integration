package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;


public class PlayerConnection extends EAObject {
    @NotNull public static final String PROP_UUID = "uuid";
    @NotNull public static final String PROP_USERNAME = "username";
    @NotNull public static final String PROP_TIMESTAMP = "timestamp";
    @NotNull public static final String PROP_TYPE = "type";

    @NotNull public final UUID uuid;
    @NotNull public final String username;
    @NotNull public final Date timestamp;
    @NotNull public final Type type;

    public PlayerConnection(@NotNull UUID uuid, @NotNull String username, @NotNull Date timestamp, @NotNull Type type) {
        this.uuid = uuid;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
    }

    public PlayerConnection(@NotNull JsonObject json) {
        this(
                UUID.fromString(json.get(PROP_UUID).getAsString()),
                json.get(PROP_USERNAME).getAsString(),
                new Date(json.get(PROP_TIMESTAMP).getAsLong()),
                Type.valueOf(json.get(PROP_TYPE).getAsString().toUpperCase()));
    }

    public enum Type {
        JOIN,
        QUIT
    }
}
