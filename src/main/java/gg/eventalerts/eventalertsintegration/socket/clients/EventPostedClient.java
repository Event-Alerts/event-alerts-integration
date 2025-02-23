package gg.eventalerts.eventalertsintegration.socket.clients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;
import gg.eventalerts.eventalertsintegration.utility.EAStringUtility;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.EventFormat;
import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.eventalertsintegration.config.PingRole;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import gg.eventalerts.eventalertsintegration.objects.Event;
import gg.eventalerts.eventalertsintegration.objects.Server;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.StringUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.util.*;

import static gg.eventalerts.eventalertsintegration.EventAlertsIntegration.MINI_MESSAGE;
import static gg.eventalerts.eventalertsintegration.utility.EventMessageUtility.*;


public class EventPostedClient extends SocketClient<Event> {
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
        final Set<PingRole> pingRoles = object.getPingRoles();
        final boolean hasRoles = !pingRoles.isEmpty();
        final Set<PingRole> ignoredPartnerRoles = plugin.config.eventMessages.ignoredPartnerRoles;
        if (hasRoles && pingRoles.stream().anyMatch(ignoredPartnerRoles::contains)) return;

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
            title = object.title.replaceAll("\\s+", " ");
        } else if (hasDescription) {
            title = description.split("\n")[0];
        }
        builder.append(Component.text(StringUtility.shorten(title.toUpperCase(), 30), NamedTextColor.GOLD, TextDecoration.BOLD));
        // roles
        if (hasRoles) {
            final TextComponent.Builder rolesComponent = Component.text().color(NamedTextColor.YELLOW);
            for (final PingRole role : pingRoles) rolesComponent.append(Component.text(" @" + role.name));
            builder.append(rolesComponent);
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
                .append(MINI_MESSAGE.deserialize("<yellow>\uD83D\uDD51 Starts in: <gold>" + DurationFormatter.formatDuration(timeUntil, "H'h' m'm' s's'")));
        // prize
        if (object.prize != null) builder
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#87ffa9>\uD83C\uDFC6 Prize: <green>" + object.prize));
        // IP
        if (object.ip != null) builder
                .append(LINE)
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>IP: <aqua>" + object.ip));
        // platform & version
        final StringBuilder platformVersion = new StringBuilder();
        if (object.platform != null) platformVersion.append(object.platform).append(" ");
        if (object.version != null) platformVersion.append(object.version);
        if (!platformVersion.isEmpty()) builder
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>Version: <aqua>" + platformVersion));
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
                    .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>Server: <aqua>" + name));
        }

        // Join button
        if (plugin.config.eventMessages.detectIps && RefPlayer.TRANSFER != null) {
            EAStringUtility.IpPort ipPort = null;
            // Get from dedicated IP field
            if (object.ip != null) ipPort = EAStringUtility.extractIpPort(object.ip, null);
            // Find in description
            if (ipPort == null && hasDescription) ipPort = EAStringUtility.extractIpPort(description, null);
            // Append to builder
            if (ipPort != null) builder.append(getJoinButton(ipPort));
        }

        // Broadcast
        broadcast(plugin, builder.append(END).build());
    }
}
