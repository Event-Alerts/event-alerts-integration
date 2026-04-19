package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.EventThreadMessage;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;

import me.clip.placeholderapi.PAPIComponents;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;


public class EventChatClient extends SocketClient<EventThreadMessage> {
    public EventChatClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.EVENT_CHAT, EventThreadMessage.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.discordMessageSyncing.enabled && (plugin.config.apiKeys.playerApiKey != null || plugin.config.apiKeys.serverApiKey != null);
    }

    @Override
    public void handle(@NotNull EventThreadMessage object) {
        // Get Minecraft player
        final Player player = object.author.player != null && object.author.player.minecraft != null ? Bukkit.getPlayer(object.author.player.minecraft.uuid()) : null;

        // Remove newlines from content
        final String contentRaw = object.content.raw
                .replace("\n", " ")
                .replace("\r", " ");
        final String contentDisplay = object.content.display
                .replace("\n", " ")
                .replace("\r", " ");
        final String contentStripped = object.content.stripped
                .replace("\n", " ")
                .replace("\r", " ");

        // Get format and plugin placeholders
        Component message = EventAlertsIntegration.MINI_MESSAGE.deserialize(plugin.config.discordMessageSyncing.format,
                Placeholder.parsed("event_id", object.event.id.toHexString()),
                Placeholder.parsed("event_type", object.event.type),
                Placeholder.parsed("event_channel", String.valueOf(object.event.channel)),
                Placeholder.parsed("event_message", object.event.message != null ? String.valueOf(object.event.message) : ""),
                Placeholder.parsed("event_control_panel", object.event.controlPanel != null ? String.valueOf(object.event.controlPanel) : ""),
                Placeholder.parsed("event_custom", String.valueOf(object.event.custom)),
                Placeholder.parsed("event_created", String.valueOf(object.event.created.getTime())),
                Placeholder.parsed("event_title", object.event.title != null ? object.event.title : object.channel.name),
                Placeholder.parsed("event_host", String.valueOf(object.event.host)),
                Placeholder.parsed("event_description", object.event.description != null ? object.event.description : ""),
                Placeholder.parsed("event_roles", object.event.roles != null ? object.event.roles.stream()
                                                                               .map(String::valueOf)
                                                                               .reduce((a, b) -> a + ", " + b)
                                                                               .orElse("") : ""),
                Placeholder.parsed("event_roles_named", object.event.rolesNamed != null ? String.join(", ", object.event.rolesNamed) : ""),
                Placeholder.parsed("event_server", object.event.server != null ? object.event.server.toHexString() : ""),
                Placeholder.parsed("event_media_name", object.event.media != null ? object.event.media.name : ""),
                Placeholder.parsed("event_ip", object.event.ip != null ? object.event.ip : ""),
                Placeholder.parsed("event_platform", object.event.platform != null ? object.event.platform : ""),
                Placeholder.parsed("event_version", object.event.version != null ? object.event.version : ""),
                Placeholder.parsed("event_prize", object.event.prize != null ? object.event.prize : ""),
                Placeholder.parsed("event_max_players", object.event.maxPlayers != null ? String.valueOf(object.event.maxPlayers) : ""),
                Placeholder.parsed("event_time", object.event.time != null ? String.valueOf(object.event.time.getTime()) : ""),
                Placeholder.parsed("event_subscribers", object.event.subscribers != null ? object.event.subscribers.stream()
                                                                                           .map(String::valueOf)
                                                                                           .reduce((a, b) -> a + ", " + b)
                                                                                           .orElse("") : ""),
                Placeholder.parsed("messageid", object.messageId),
                Placeholder.parsed("channel_id", object.channel.id),
                Placeholder.parsed("channel_name", object.channel.name),
                Placeholder.parsed("author_id", object.author.id),
                Placeholder.parsed("author_name", object.author.name),
                Placeholder.parsed("author_effectivename", object.author.effectiveName),
                Placeholder.parsed("content_raw", contentRaw),
                Placeholder.parsed("content_display", contentDisplay),
                Placeholder.parsed("content_stripped", contentStripped),
                Placeholder.parsed("player_name", player != null ? player.getName() : ""));

        // PlaceholderAPI
        if (plugin.papiInstalled) message = PAPIComponents.setPlaceholders(player, message);

        // Send message
        Bukkit.broadcast(message);
    }
}
