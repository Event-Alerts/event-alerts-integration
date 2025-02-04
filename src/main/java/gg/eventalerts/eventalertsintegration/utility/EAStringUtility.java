package gg.eventalerts.eventalertsintegration.utility;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EAStringUtility {
    @NotNull private static final Pattern EMOJI_PATTERN = Pattern.compile(":([a-zA-Z0-9_]+):");
    @NotNull private static final Pattern IP_PATTERN = Pattern.compile("((?:[a-zA-Z\\d](?:[a-zA-Z\\d-]*[a-zA-Z\\d])?\\.)+[a-zA-Z]{2,}|(?:\\d{1,3}\\.){3}\\d{1,3})(:\\d{1,5})?");

    @NotNull
    public static String replaceEmojis(@NotNull EventAlertsIntegration plugin, @NotNull String string) {
        final Matcher emojiMatcher = EMOJI_PATTERN.matcher(string);
        if (!emojiMatcher.find()) return string;
        emojiMatcher.reset();

        // Load JEmoji library for EmojiManager
        if (!plugin.libraryManager.isLoaded(EALibrary.JEMOJI)) plugin.libraryManager.loadLibrary(EALibrary.JEMOJI);

        // Replace emojis
        final StringBuilder description = new StringBuilder();
        while (emojiMatcher.find()) {
            emojiMatcher.appendReplacement(description, EmojiManager.getByDiscordAlias(emojiMatcher.group(1))
                    .map(Emoji::getEmoji)
                    .orElse(emojiMatcher.group()));
        }
        emojiMatcher.appendTail(description);
        return description.toString();
    }

    @Nullable
    public static IpPort extractIpPort(@NotNull String string) {
        String ip = null;
        int port = 25565;
        final Matcher matcher = IP_PATTERN.matcher(string);
        if (matcher.find()) {
            ip = matcher.group(1);
            final String portString = matcher.group(2);
            if (portString != null) port = Mapper.toInt(portString.substring(1)).orElse(25565);
        }
        return ip != null ? new IpPort(ip, port) : null;
    }

    public static class IpPort {
        @NotNull public final String ip;
        public final int port;

        public IpPort(@NotNull String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
