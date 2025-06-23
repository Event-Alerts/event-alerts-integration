package gg.eventalerts.eventalertsintegration.config;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public enum PingRole {
    COMMUNITY,
    PARTNER,
    MONEY(true),
    FUN(true),
    HOUSING(true),
    CIVILIZATION(true);

    @NotNull public static final Set<PingRole> PARTNER_PINGABLE = Arrays.stream(values())
            .filter(role -> role.partnerPingable)
            .collect(Collectors.toSet());

    public final boolean partnerPingable;
    @NotNull public final String name = StringUtils.capitalize(name().toLowerCase());

    PingRole() {
        this(false);
    }

    PingRole(boolean partnerPingable) {
        this.partnerPingable = partnerPingable;
    }
}
