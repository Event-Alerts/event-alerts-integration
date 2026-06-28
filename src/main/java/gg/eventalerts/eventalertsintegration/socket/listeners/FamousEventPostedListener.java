package gg.eventalerts.eventalertsintegration.socket.listeners;

import gg.eventalerts.eventalertsintegration.IDMappings;
import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;
import gg.eventalerts.eventalertsintegration.utility.EAStringUtility;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.sdk.object.EAFamousEvent;
import gg.eventalerts.sdk.websocket.handler.FamousEventPostedHandler;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
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


public class FamousEventPostedListener extends FamousEventPostedHandler {
    @NotNull private final Pattern ID_PATTERN = Pattern.compile("<@[!&]?(\\d+)>|<#(\\d+)>");
    @Nullable private static final TextColor MENTION_HIGHLIGHT = TextColor.color(215, 215, 215);

    @NotNull private final EventAlertsIntegration plugin;

    public FamousEventPostedListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldSubscribe() {
        return plugin.config.event_messages.enabled && !plugin.config.event_messages.ignored_types.containsAll(EventType.FAMOUS_TYPES);
    }

    @Override
    public void onMessage(@NotNull SocketEvent<EAFamousEvent> event) {
        final EAFamousEvent famousEvent = event.data;
        if (famousEvent == null || famousEvent.type == null || famousEvent.message == null) {
            AnnoyingPlugin.log(Level.WARNING, "Invalid FamousEvent: " + famousEvent);
            return;
        }

        // Check type
        if (plugin.config.event_messages.ignored_types.contains(famousEvent.type)) return;

        // Build message
        final TextComponent.Builder builder = Component.text()
                .append(BEGINNING)
                .append(LINE)
                .append(Component.text("NEW " + famousEvent.type.name() + " EVENT!", NamedTextColor.GOLD, TextDecoration.BOLD));

        // Replace emojis
        final String message = EAStringUtility.replaceEmojis(plugin, famousEvent.message);

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
                        .map(IDMappings.ID_MAPPINGS::get)
                        .orElse(null);
                if (name == null) {
                    builder.append(Component.text(word + " ", NamedTextColor.GRAY));
                    continue;
                }
                builder.append(Component.text("@" + name + " ", MENTION_HIGHLIGHT));
            }
        }

        // Join button
        if (plugin.config.event_messages.detect_ips && RefPlayer.TRANSFER != null) {
            final EAStringUtility.IpPort ipPort = EAStringUtility.extractIpPort(message, "invadedlands.net");
            if (ipPort != null) builder.append(getJoinButton(ipPort));
        }

        // Website link
        builder.append(getWebsiteLink(null));

        // Broadcast
        broadcast(plugin, builder.append(END).build());
    }
}
