package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;
import gg.eventalerts.eventalertsintegration.utility.EAStringUtility;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.FamousEvent;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gg.eventalerts.eventalertsintegration.utility.EventMessageUtility.*;


public class FamousEventPostedClient extends SocketClient<FamousEvent> {
    @NotNull private final Pattern ID_PATTERN = Pattern.compile("<@[!&]?(\\d+)>|<#(\\d+)>");
    @Nullable private static final TextColor MENTION_HIGHLIGHT = TextColor.color(215, 215, 215);

    public FamousEventPostedClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.FAMOUS_EVENT_POSTED, FamousEvent.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.eventMessages.enabled && !plugin.config.eventMessages.ignoredTypes.containsAll(EventType.FAMOUS_TYPES);
    }

    @Override
    public void handle(@NotNull FamousEvent object) {
        if (object.type == null) {
            AnnoyingPlugin.log(Level.WARNING, "Invalid FamousEvent: " + object);
            return;
        }

        // Check type
        if (plugin.config.eventMessages.ignoredTypes.contains(object.type)) return;

        // Build message
        final TextComponent.Builder builder = Component.text()
                .append(BEGINNING)
                .append(LINE)
                .append(Component.text("NEW " + object.type.name() + " EVENT!", NamedTextColor.GOLD, TextDecoration.BOLD));

        // Replace emojis
        final String message = EAStringUtility.replaceEmojis(plugin, object.message);

        // Append message to builder
        for (final String line : message.split("\n")) {
            builder.append(LINE);
            for (final String word : line.split(" ")) {
                final Matcher matcher = ID_PATTERN.matcher(word);
                if (!matcher.matches()) {
                    builder.append(Component.text(word + " ", NamedTextColor.GRAY));
                    continue;
                }
                // Replace IDs with known mappings
                final String name = Mapper.toLong(matcher.group(1))
                        .map(EventAlertsIntegration.ID_MAPPINGS::get)
                        .orElse(null);
                if (name == null) {
                    builder.append(Component.text(word + " ", NamedTextColor.GRAY));
                    continue;
                }
                builder.append(Component.text("@" + name + " ", MENTION_HIGHLIGHT));
            }
        }

        // Join button
        if (plugin.config.eventMessages.detectIps && RefPlayer.TRANSFER != null) {
            final EAStringUtility.IpPort ipPort = EAStringUtility.extractIpPort(message, "invadedlands.net");
            if (ipPort != null) builder.append(getJoinButton(ipPort));
        }

        // Broadcast
        broadcast(plugin, builder.append(END).build());
    }
}
