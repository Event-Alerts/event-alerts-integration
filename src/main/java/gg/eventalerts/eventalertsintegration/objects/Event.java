package gg.eventalerts.eventalertsintegration.objects;

import gg.eventalerts.eventalertsintegration.config.PingRole;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;


public class Event extends EAObject {
    // BUILDER/CUSTOM
    @Nullable public ObjectId id;
    @Nullable public String type;
    @Nullable public Long channel;
    @Nullable public Long message;
    @Nullable public Long controlPanel;
    @Nullable public Boolean custom;
    @Nullable public Date created;
    @Nullable public String title;
    @Nullable public Long host;
    @Nullable public String description;
    @Nullable public Set<Long> roles;
    @Nullable public Set<PingRole> rolesNamed;
    @Nullable public ObjectId server;
    @Nullable public CachedMedia media;
    // BUILDER
    @Nullable public String ip;
    @Nullable public Set<Platform> platforms;
    @Nullable public String version;
    @Nullable public String prize;
    @Nullable public Integer maxPlayers;
    /**
     * The time the event starts
     */
    @Nullable public Date time;
    @Nullable public Set<Long> subscribers;

    public boolean custom() {
        return custom != null && custom;
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

    public static class CachedMedia extends EAObject {
        @Nullable public String name;
    }
}
