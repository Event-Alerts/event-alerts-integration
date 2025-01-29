package gg.eventalerts.eventalertsintegration.config;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public enum EventType {
    SKEPPY(true),
    POTENTIAL_FAMOUS(true, "POTENTIAL FAMOUS"),
    SIGHTING(true, "SKEPPY SIGHTING"),
    FAMOUS(true),
    PARTNER,
    COMMUNITY;

    @NotNull public static final Set<EventType> FAMOUS_TYPES = Arrays.stream(EventType.values())
            .filter(eventType -> eventType.isFamous)
            .collect(Collectors.toSet());
    @NotNull public static final Set<EventType> REGULAR_TYPES = Arrays.stream(EventType.values())
            .filter(eventType -> !eventType.isFamous)
            .collect(Collectors.toSet());

    public final boolean isFamous;
    @NotNull public String name = name();

    EventType(boolean isFamous, @NotNull String name) {
        this.isFamous = isFamous;
        this.name = name;
    }

    EventType(boolean isFamous) {
        this.isFamous = isFamous;
    }

    EventType() {
        this(false);
    }
}
