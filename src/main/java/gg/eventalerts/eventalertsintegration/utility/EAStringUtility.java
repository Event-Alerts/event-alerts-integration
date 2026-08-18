package gg.eventalerts.eventalertsintegration.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EAStringUtility {
    @NotNull private static final Pattern IP_PATTERN = Pattern.compile("((?:[a-zA-Z\\d](?:[a-zA-Z\\d-]*[a-zA-Z\\d])?\\.)+[a-zA-Z]{2,}|(?:\\d{1,3}\\.){3}\\d{1,3})(:\\d{1,5})?");

    @Nullable
    public static IpPort extractIpPort(@NotNull String string, @Nullable String defaultIp) {
        String ip = defaultIp;
        int port = 25565;
        final Matcher matcher = IP_PATTERN.matcher(string);
        if (matcher.find()) {
            ip = matcher.group(1);
            final String portString = matcher.group(2);
            if (portString != null) port = Mapper.toInt(portString.substring(1)).orElse(25565);
        }
        return ip != null ? new IpPort(ip, port) : null;
    }

    public record IpPort(@NotNull String ip, int port) {}
}
