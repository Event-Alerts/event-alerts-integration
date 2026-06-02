package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.config.PingRole;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class Event extends EAObject {
    // BUILDER/CUSTOM
    @Nullable public final ObjectId id;
    @Nullable public final String type;
    @Nullable public final Long channel;
    @Nullable public final Long message;
    @Nullable public final Long controlPanel;
    public final boolean custom;
    @Nullable public final Date created;
    @Nullable public final String title;
    public final long host;
    @Nullable public final String description;
    @Nullable public final Set<Long> roles;
    @Nullable public final Set<PingRole> rolesNamed;
    @Nullable public final ObjectId server;
    @Nullable public final CachedMedia media;
    // BUILDER
    @Nullable public final String ip;
    @Nullable public final Set<Platform> platforms;
    @Nullable public final String version;
    @Nullable public final String prize;
    @Nullable public final Integer maxPlayers;
    /**
     * The time the event starts
     */
    @Nullable public final Date time;
    @Nullable public final Set<Long> subscribers;

    public Event(@NotNull JsonObject json) {
        id = MiscUtility.handleException(() -> GSONProvider.GSON.fromJson(json.get("id"), ObjectId.class)).orElse(null);
        type = MiscUtility.handleException(() -> json.get("type").getAsString()).orElse(null);
        channel = MiscUtility.handleException(() -> json.get("channel").getAsLong()).orElse(null);
        message = json.has("message") ? MiscUtility.handleException(() -> json.get("message").getAsLong()).orElse(null) : null;
        controlPanel = json.has("controlPanel") ? MiscUtility.handleException(() -> json.get("controlPanel").getAsLong()).orElse(null) : null;
        custom = json.get("custom").getAsBoolean();
        created = MiscUtility.handleException(() -> GSONProvider.GSON.fromJson(json.get("created"), Date.class)).orElse(null);
        title = json.has("title") ? MiscUtility.handleException(() -> json.get("title").getAsString()).orElse(null) : null;
        host = json.get("host").getAsLong();
        description = json.has("description") ? MiscUtility.handleException(() -> json.get("description").getAsString()).orElse(null) : null;
        if (json.has("roles")) {
            roles = new HashSet<>();
            try {
                for (final JsonElement role : json.getAsJsonArray("roles")) roles.add(role.getAsLong());
            } catch (final Exception ignored) {}
        } else {
            roles = null;
        }
        if (json.has("rolesNamed")) {
            rolesNamed = MiscUtility.handleException(() -> {
                final Set<PingRole> parsedRoles = new HashSet<>();
                for (final JsonElement role : json.getAsJsonArray("rolesNamed")) {
                    final PingRole parsedRole = GSONProvider.GSON.fromJson(role, PingRole.class);
                    if (parsedRole != null) parsedRoles.add(parsedRole);
                }
                return parsedRoles;
            }).orElse(null);
        } else {
            rolesNamed = null;
        }
        server = json.has("server") ? MiscUtility.handleException(() -> GSONProvider.GSON.fromJson(json.get("server"), ObjectId.class)).orElse(null) : null;
        media = json.has("media") ? MiscUtility.handleException(() -> new CachedMedia(json.getAsJsonObject("media"))).orElse(null) : null;
        ip = json.has("ip") ? MiscUtility.handleException(() -> json.get("ip").getAsString()).orElse(null) : null;
        if (json.has("platforms")) {
            platforms = MiscUtility.handleException(() -> {
                final Set<Platform> parsedPlatforms = new HashSet<>();
                for (final JsonElement element : json.getAsJsonArray("platforms")) {
                    final Platform parsedPlatform = GSONProvider.GSON.fromJson(element, Platform.class);
                    if (parsedPlatform != null) parsedPlatforms.add(parsedPlatform);
                }
                return parsedPlatforms;
            }).orElse(null);
        } else {
            platforms = null;
        }
        version = json.has("version") ? MiscUtility.handleException(() -> json.get("version").getAsString()).orElse(null) : null;
        prize = json.has("prize") ? MiscUtility.handleException(() -> json.get("prize").getAsString()).orElse(null) : null;
        maxPlayers = json.has("maxPlayers") ? MiscUtility.handleException(() -> json.get("maxPlayers").getAsInt()).orElse(null) : null;
        time = json.has("time") ? MiscUtility.handleException(() -> GSONProvider.GSON.fromJson(json.get("time"), Date.class)).orElse(null) : null;
        if (json.has("subscribers")) {
            subscribers = new HashSet<>();
            try {
                for (final JsonElement element : json.getAsJsonArray("subscribers")) subscribers.add(element.getAsLong());
            } catch (final Exception ignored) {}
        } else {
            subscribers = null;
        }
    }

    @Nullable
    public Long getTimeUntil() {
        return time == null ? null : time.getTime() - System.currentTimeMillis();
    }

    public enum Platform {
        JAVA("Java"),
        BEDROCK("Bedrock");

        @NotNull private final String name;

        Platform(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public static String toString(@Nullable Set<Platform> platforms) {
            if (platforms == null) return "";
            return platforms.stream()
                    .sorted(Comparator.comparingInt(Enum::ordinal))
                    .map(platform -> platform.name)
                    .reduce((a, b) -> a + "/" + b)
                    .orElse("");
        }
    }

    public static class CachedMedia {
        @NotNull public final String name;

        public CachedMedia(@NotNull JsonObject json) {
            this.name = json.get("name").getAsString();
        }
    }
}
