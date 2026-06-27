package gg.eventalerts.eventalertsintegration.object.sdk;

import gg.eventalerts.sdk.object.EAEvent;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;


public class EventUtility {
    public static class PingRole {
        @NotNull public static final Set<EAEvent.PingRole> PARTNER_PINGABLE = Arrays.stream(EAEvent.PingRole.values())
                .filter(role -> role.partnerToggleable)
                .collect(Collectors.toSet());

        @NotNull
        public static String getName(@NotNull EAEvent.PingRole pingRole) {
            return StringUtils.capitalize(pingRole.name().toLowerCase());
        }
    }

    public static class Platform {
        @NotNull
        public static String getName(@NotNull EAEvent.Platform platform) {
            return StringUtils.capitalize(platform.name().toLowerCase());
        }

        @NotNull
        public static String toString(@Nullable Collection<EAEvent.Platform> platforms) {
            if (platforms == null) return "";
            return platforms.stream()
                    .sorted(Comparator.comparingInt(Enum::ordinal))
                    .map(Platform::getName)
                    .reduce((a, b) -> a + "/" + b)
                    .orElse("");
        }
    }
}
