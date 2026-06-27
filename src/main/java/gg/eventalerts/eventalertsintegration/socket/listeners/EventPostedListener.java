package gg.eventalerts.eventalertsintegration.socket.listeners;

import gg.eventalerts.eventalertsintegration.object.sdk.EventUtility;
import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;
import gg.eventalerts.eventalertsintegration.utility.EAStringUtility;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.EventFormat;
import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.handler.EventPostedHandler;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.libs.javautilities.StringUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.util.Objects;
import java.util.Set;

import static gg.eventalerts.eventalertsintegration.EventAlertsIntegration.MINI_MESSAGE;
import static gg.eventalerts.eventalertsintegration.utility.EventMessageUtility.*;


public class EventPostedListener extends EventPostedHandler {
    @NotNull private final EventAlertsIntegration plugin;

    public EventPostedListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldSubscribe() {
        return plugin.config.event_messages.enabled && !plugin.config.event_messages.ignored_types.containsAll(EventType.REGULAR_TYPES);
    }

    @Override
    public void onMessage(@NotNull SocketEvent<EAEvent> event) {
        final EAEvent eaEvent = event.data;
        if (eaEvent == null) return;

        // Get/check type
        final EventType type = eaEvent.server != null ? EventType.PARTNER : EventType.COMMUNITY;
        if (plugin.config.event_messages.ignored_types.contains(type)) return;

        // Get/check format
        final EventFormat format = Boolean.TRUE.equals(eaEvent.custom) ? EventFormat.CUSTOM : EventFormat.BUILT;
        if (plugin.config.event_messages.ignored_formats.contains(format)) return;

        // Check server
        if (eaEvent.server != null && !plugin.config.event_messages.isInHostFilter(eaEvent.server)) return;

        // Check host
        if (eaEvent.host != null && !plugin.config.event_messages.isInHostFilter(eaEvent.host)) return;

        // Check Partner ping roles
        final boolean hasRoles = eaEvent.rolesNamed != null && !eaEvent.rolesNamed.isEmpty();
        final Set<EAEvent.PingRole> ignoredPartnerRoles = plugin.config.event_messages.ignored_partner_roles;
        if (hasRoles && eaEvent.rolesNamed.stream().anyMatch(ignoredPartnerRoles::contains)) return;

        // Replace emojis in description
        String description = eaEvent.description;
        final boolean hasDescription = description != null;
        if (hasDescription) description = EAStringUtility.replaceEmojis(plugin, description);

        // Build message
        final TextComponent.Builder builder = Component.text()
                .append(BEGINNING)
                .append(LINE);

        // Title
        String title = "New event!";
        if (eaEvent.title != null) {
            title = eaEvent.title.replaceAll("\\s+", " ");
        } else if (hasDescription) {
            title = description.split("\n")[0];
        }
        builder.append(Component.text(StringUtility.shorten(title.toUpperCase(), 30), NamedTextColor.GOLD, TextDecoration.BOLD));
        // roles
        if (hasRoles) {
            final TextComponent.Builder rolesComponent = Component.text().color(NamedTextColor.YELLOW);
            for (final EAEvent.PingRole role : eaEvent.rolesNamed) rolesComponent.append(Component.text(" @" + EventUtility.PingRole.getName(role)));
            builder.append(rolesComponent);
        }
        // description
        if (hasDescription) for (final String descriptionLine : description.split("\n")) builder
                .append(LINE)
                .append(Component.text(descriptionLine, NamedTextColor.GRAY));
        // time
        final Long timeUntil = eaEvent.getTimeUntil();
        if (timeUntil != null) builder
                .append(LINE)
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<yellow>\uD83D\uDD51 Starts in: <gold>" + DurationFormatter.formatDuration(timeUntil, "H'h' m'm' s's'")));
        // prize
        if (eaEvent.prize != null) builder
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#87ffa9>\uD83C\uDFC6 Prize: "))
                .append(Component.text(eaEvent.prize, NamedTextColor.GREEN));
        // IP
        if (eaEvent.ip != null) builder
                .append(LINE)
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>IP: "))
                .append(Component.text(eaEvent.ip, NamedTextColor.AQUA));
        // platform & version
        final StringBuilder platformVersion = new StringBuilder();
        if (eaEvent.platforms != null && !eaEvent.platforms.isEmpty()) platformVersion.append(EventUtility.Platform.toString(eaEvent.platforms)).append(" ");
        if (eaEvent.version != null) platformVersion.append(eaEvent.version);
        if (!platformVersion.isEmpty()) builder
                .append(LINE)
                .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>Version: "))
                .append(Component.text(platformVersion.toString(), NamedTextColor.AQUA));
        // server
        if (eaEvent.server != null) {
            // Get name from API
            plugin.http.partnerServers.retrieveOneById(eaEvent.server)
                    .map(server -> server.name)
                    .onErrorReturnNull()
                    .ifPresent(name -> builder
                                .append(LINE)
                                .append(MINI_MESSAGE.deserialize("<#88a7b5>» <#bfebff>Server: "))
                                .append(Component.text(Objects.requireNonNull(name), NamedTextColor.AQUA)));
        }

        // Join button
        if (plugin.config.event_messages.detect_ips && RefPlayer.TRANSFER != null) {
            EAStringUtility.IpPort ipPort = null;
            // Get from dedicated IP field
            if (eaEvent.ip != null) ipPort = EAStringUtility.extractIpPort(eaEvent.ip, null);
            // Find in description
            if (ipPort == null && hasDescription) ipPort = EAStringUtility.extractIpPort(description, null);
            // Append to builder
            if (ipPort != null) builder.append(getJoinButton(ipPort));
        }

        // Broadcast
        broadcast(plugin, builder.append(END).build());
    }
}
