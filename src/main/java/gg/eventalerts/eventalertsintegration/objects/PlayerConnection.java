package gg.eventalerts.eventalertsintegration.objects;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;


public class PlayerConnection extends EAObject {
    @Nullable public UUID uuid;
    @Nullable public String username;
    @Nullable public Date timestamp;
    @Nullable public Type type;

    public PlayerConnection() {}

    public PlayerConnection(@Nullable UUID uuid, @Nullable String username, @Nullable Date timestamp, @Nullable Type type) {
        this.uuid = uuid;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
    }

    public enum Type {
        JOIN,
        QUIT
    }
}
