package gg.eventalerts.eventalertsintegration.config;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public enum PartnerPingRole {
    MONEY(970434305203511359L),
    FUN(970434303391576164L),
    HOUSING(970434294893928498L),
    CIVILIZATION(1134932175821734119L);

    public final long id;
    @NotNull public final String name = StringUtils.capitalize(name().toLowerCase());

    PartnerPingRole(long id) {
        this.id = id;
    }

    @Nullable
    public static PartnerPingRole fromId(long id) {
        for (final PartnerPingRole role : values()) if (role.id == id) return role;
        return null;
    }
}
