package gg.eventalerts.eventalertsintegration.config;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public enum PingRole {
    COMMUNITY("Community"),
    PARTNER("Partner"),
    BIG_MONEY("Big Money", true),
    MONEY("Money", true),
    FUN("Fun", true),
    HOUSING("Housing", true),
    CIVILIZATION("Civilization", true);

    @NotNull public static final Set<PingRole> PARTNER_PINGABLE = Arrays.stream(values())
            .filter(role -> role.partnerPingable)
            .collect(Collectors.toSet());

    @NotNull public final String name;
    public final boolean partnerPingable;

    PingRole(@NotNull String name) {
        this(name, false);
    }

    PingRole(@NotNull String name, boolean partnerPingable) {
        this.name = name;
        this.partnerPingable = partnerPingable;
    }
}
