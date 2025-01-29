package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import org.bson.types.ObjectId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;


public class Event extends EAObject {
    public final boolean custom;
    @Nullable public final ObjectId server;
    @Nullable public final String title;
    @Nullable public final String description;
    @Nullable public final Set<Long> roles;
    // BUILDER
    @Nullable public final String ip;
    @Nullable public final String platform;
    @Nullable public final String version;
    @Nullable public final String prize;
    /**
     * The time the event starts
     */
    @Nullable public final Date time;

    public Event(@NotNull JsonObject json) {
        super(json);
        custom = json.get("custom").getAsBoolean();
        server = json.has("server") ? new ObjectId(json.get("server").getAsString()) : null;
        title = json.has("title") ? json.get("title").getAsString() : null;
        description = json.has("description") ? json.get("description").getAsString() : null;
        roles = json.has("roles") ? toLongSet(json.getAsJsonArray("roles")) : null;
        ip = json.has("ip") ? json.get("ip").getAsString() : null;
        platform = json.has("platform") ? json.get("platform").getAsString() : null;
        version = json.has("version") ? json.get("version").getAsString() : null;
        prize = json.has("prize") ? json.get("prize").getAsString() : null;
        time = json.has("time") ? new Date(json.get("time").getAsLong()) : null;
    }

    @Nullable
    public Long getTimeUntil() {
        return time == null ? null : time.getTime() - System.currentTimeMillis();
    }
}
