package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.PingRole;

import org.bson.types.ObjectId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class Event extends EAObject {
    // BUILDER/CUSTOM
    @NotNull public final ObjectId id;
    @NotNull public final String type;
    public final long channel;
    @Nullable public final Long message;
    @Nullable public final Long controlPanel;
    public final boolean custom;
    @NotNull public final Date created;
    @Nullable public final String title;
    public final long host;
    @Nullable public final String description;
    @Nullable public final Set<Long> roles;
    @Nullable public final Set<String> rolesNamed;
    @Nullable public final ObjectId server;
    @Nullable public final CachedMedia media;
    // BUILDER
    @Nullable public final String ip;
    @Nullable public final String platform;
    @Nullable public final String version;
    @Nullable public final String prize;
    @Nullable public final Integer maxPlayers;
    /**
     * The time the event starts
     */
    @Nullable public final Date time;
    @Nullable public final Set<Long> subscribers;

    public Event(@NotNull JsonObject json) {
        id = new ObjectId(json.get("id").getAsString());
        type = json.get("type").getAsString();
        channel = json.get("channel").getAsLong();
        message = json.has("message") ? json.get("message").getAsLong() : null;
        controlPanel = json.has("controlPanel") ? json.get("controlPanel").getAsLong() : null;
        custom = json.get("custom").getAsBoolean();
        created = new Date(json.get("created").getAsLong());
        title = json.has("title") ? json.get("title").getAsString() : null;
        host = json.get("host").getAsLong();
        description = json.has("description") ? json.get("description").getAsString() : null;
        if (json.has("roles")) {
            roles = new HashSet<>();
            for (final JsonElement element : json.getAsJsonArray("roles")) roles.add(element.getAsLong());
        } else {
            roles = null;
        }
        if (json.has("rolesNamed")) {
            rolesNamed = new HashSet<>();
            for (final JsonElement element : json.getAsJsonArray("rolesNamed")) rolesNamed.add(element.getAsString());
        } else {
            rolesNamed = null;
        }
        server = json.has("server") ? new ObjectId(json.get("server").getAsString()) : null;
        media = json.has("media") ? new CachedMedia(json.getAsJsonObject("media")) : null;
        ip = json.has("ip") ? json.get("ip").getAsString() : null;
        platform = json.has("platform") ? json.get("platform").getAsString() : null;
        version = json.has("version") ? json.get("version").getAsString() : null;
        prize = json.has("prize") ? json.get("prize").getAsString() : null;
        maxPlayers = json.has("maxPlayers") ? json.get("maxPlayers").getAsInt() : null;
        time = json.has("time") ? new Date(json.get("time").getAsLong()) : null;
        if (json.has("subscribers")) {
            subscribers = new HashSet<>();
            for (final JsonElement element : json.getAsJsonArray("subscribers")) subscribers.add(element.getAsLong());
        } else {
            subscribers = null;
        }
    }

    @Nullable
    public Long getTimeUntil() {
        return time == null ? null : time.getTime() - System.currentTimeMillis();
    }

    @NotNull
    public Set<PingRole> getPingRoles() {
        if (rolesNamed == null) return Set.of();
        final Set<PingRole> pingRoles = new HashSet<>();
        for (final String role : rolesNamed) {
            final PingRole pingRole = EventAlertsIntegration.getEnum(PingRole.class, role);
            if (pingRole != null) pingRoles.add(pingRole);
        }
        return pingRoles;
    }

    public static class CachedMedia {
        @NotNull public final String name;

        public CachedMedia(@NotNull JsonObject json) {
            this.name = json.get("name").getAsString();
        }
    }
}
