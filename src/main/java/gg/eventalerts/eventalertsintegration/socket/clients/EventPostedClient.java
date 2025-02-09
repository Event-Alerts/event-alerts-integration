package gg.eventalerts.eventalertsintegration.socket.clients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.utility.EAStringUtility;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.EventFormat;
import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.eventalertsintegration.config.PartnerPingRole;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import gg.eventalerts.eventalertsintegration.objects.Event;
import gg.eventalerts.eventalertsintegration.objects.Server;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.WebSocketClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.StringUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.util.*;

import static gg.eventalerts.eventalertsintegration.utility.EventMessageUtility.*;


public class EventPostedClient extends WebSocketClient<Event> {
    public EventPostedClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.EVENT_POSTED, Event.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.eventMessages.enabled && !plugin.config.eventMessages.ignoredTypes.containsAll(EventType.REGULAR_TYPES);
    }

    @Override
    public void handle(@NotNull Event object) {
        // Get/check type
        final EventType type = object.server != null ? EventType.PARTNER : EventType.COMMUNITY;
        if (plugin.config.eventMessages.ignoredTypes.contains(type)) return;

        // Get/check format
        final EventFormat format = object.custom ? EventFormat.CUSTOM : EventFormat.BUILT;
        if (plugin.config.eventMessages.ignoredFormats.contains(format)) return;

        // Check server
        if (object.server != null && !plugin.config.eventMessages.hostFilterServers.isEmpty() && !plugin.config.eventMessages.hostFilterServers.contains(object.server.toString())) return;

        // Check host
        if (!plugin.config.eventMessages.hostFilterUsers.isEmpty() && !plugin.config.eventMessages.hostFilterUsers.contains(object.host)) return;

        // Check Partner ping roles
        final List<PartnerPingRole> partnerRoles = object.roles == null ? List.of() : object.roles.stream()
                .map(PartnerPingRole::fromId)
                .filter(Objects::nonNull)
                .toList();
        final Set<PartnerPingRole> ignoredPartnerRoles = plugin.config.eventMessages.ignoredPartnerRoles;
        if (!partnerRoles.isEmpty() && partnerRoles.stream().anyMatch(ignoredPartnerRoles::contains)) return;
        final int roleCount = partnerRoles.size();

        // Replace emojis in description
        String description = object.description;
        final boolean hasDescription = description != null;
        if (hasDescription) description = EAStringUtility.replaceEmojis(plugin, description);

        // Build message
        final TextComponent.Builder builder = Component.text()
                .append(BEGINNING)
                .append(LINE);

        // Title
        String title = "New event!";
        if (object.title != null) {
            title = object.title;
        } else if (hasDescription) {
            title = StringUtility.shorten(description, 25);
        }
        builder.append(Component.text(title.toUpperCase(), NamedTextColor.GOLD, TextDecoration.BOLD));
        // roles
        if (roleCount > 0) {
            final String role2 = roleCount > 1 ? partnerRoles.get(1).name : null;
            builder.append(Component.text(" | @" + partnerRoles.get(0).name + (role2 != null ? " @" + role2 : ""), NamedTextColor.YELLOW));
        }
        // description
        if (hasDescription) for (final String descriptionLine : description.split("\n")) builder
                .append(LINE)
                .append(Component.text(descriptionLine, NamedTextColor.GRAY));
        // time
        final Long timeUntil = object.getTimeUntil();
        if (timeUntil != null) builder
                .append(LINE)
                .append(LINE)
                .append(Component.text("\uD83D\uDD51 Starts in: ", NamedTextColor.YELLOW))
                .append(Component.text(DurationFormatter.formatDuration(timeUntil, "H'h' m'm' s's'"), NamedTextColor.GOLD));
        // prize
        if (object.prize != null) builder
                .append(LINE)
                .append(Component.text("\uD83C\uDFC6 Prize: ", TextColor.fromCSSHexString("#87ffa9")))
                .append(Component.text(object.prize, NamedTextColor.GREEN));
        // IP
        if (object.ip != null) builder
                .append(LINE)
                .append(LINE)
                .append(Component.text("» ", TextColor.fromCSSHexString("#88a7b5")))
                .append(Component.text("IP: ", TextColor.fromCSSHexString("#bfebff")))
                .append(Component.text(object.ip, NamedTextColor.AQUA));
        // platform & version
        final StringBuilder platformVersion = new StringBuilder();
        if (object.platform != null) platformVersion.append(object.platform).append(" ");
        if (object.version != null) platformVersion.append(object.version);
        if (!platformVersion.isEmpty()) builder
                .append(LINE)
                .append(Component.text("» ", TextColor.fromCSSHexString("#88a7b5")))
                .append(Component.text("Version: ", TextColor.fromCSSHexString("#bfebff")))
                .append(Component.text(platformVersion.toString(), NamedTextColor.AQUA));
        // server
        if (object.server != null) {
            // Get name from API
            String name = null;
            final JsonObject json = HttpUtility
                    .getJson(plugin.getUserAgent(), plugin.getApiHost() + "servers/id/" + object.server)
                    .map(JsonElement::getAsJsonObject)
                    .orElse(null);
            if (json != null && json.has("server")) {
                final JsonElement serverElement = json.get("server");
                if (serverElement.isJsonObject()) {
                    final Server server = EAObject.newObject(plugin, Server.class, serverElement.getAsJsonObject());
                    if (server != null) name = server.name;
                }
            }

            // Append to builder
            if (name != null) builder
                    .append(LINE)
                    .append(Component.text("» ", TextColor.fromCSSHexString("#88a7b5")))
                    .append(Component.text("Server: ", TextColor.fromCSSHexString("#bfebff")))
                    .append(Component.text(name, NamedTextColor.AQUA));
        }

        // Join button
        if (plugin.config.eventMessages.detectIps) {
            EAStringUtility.IpPort ipPort = null;
            // Get from dedicated IP field
            if (object.ip != null) ipPort = EAStringUtility.extractIpPort(object.ip);
            // Find in description
            if (ipPort == null && hasDescription) ipPort = EAStringUtility.extractIpPort(description);
            // Append to builder
            if (ipPort != null) builder.append(getJoinButton(ipPort));
        }

        // Broadcast
        broadcast(builder.append(END).build());
    }
}
