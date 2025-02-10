package gg.eventalerts.eventalertsintegration.config;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;


public enum PingRole {
    COMMUNITY,
    PARTNER,
    MONEY,
    FUN,
    HOUSING,
    CIVILIZATION;

    @NotNull public final String name = StringUtils.capitalize(name().toLowerCase());
}
