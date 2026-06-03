package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.Event;
import gg.eventalerts.eventalertsintegration.objects.EventThreadMessage;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import me.clip.placeholderapi.PAPIComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
        return plugin.config.syncing.discord_to_minecraft.messages.enabled && (plugin.config.api_keys.player != null || plugin.config.api_keys.server != null);
    }

    @Override
    public void onMessage(@NotNull EventThreadMessage object) {
        if (!Bukkit.isPrimaryThread()) {
            plugin.scheduler.runSync(() -> onMessage(object));
            return;
        }
        
        // Get Minecraft player
        final Player player = object.author != null && object.author.player != null && object.author.player.minecraft != null && object.author.player.minecraft.uuid != null ? Bukkit.getPlayer(object.author.player.minecraft.uuid) : null;

        // Remove newlines from content
        final boolean hasMessage = object.message != null;
        final boolean hasMessageContent = hasMessage && object.message.content != null;
        final String contentRaw = !hasMessageContent || object.message.content.raw == null ? "" : object.message.content.raw
                .replace("\n", " ")
                .replace("\r", " ");
        final String contentDisplay = !hasMessageContent || object.message.content.display == null ? "" : object.message.content.display
                .replace("\n", " ")
                .replace("\r", " ");
        final String contentStripped = !hasMessageContent || object.message.content.stripped == null ? "" : object.message.content.stripped
                .replace("\n", " ")
                .replace("\r", " ");

        // Build message_attachments_pretty
        final StringBuilder messageAttachmentsPretty = new StringBuilder();
        final boolean hasMessageAttachments = hasMessage && object.message.attachments != null && !object.message.attachments.isEmpty();
        if (hasMessageAttachments) {
            // Trailing 0 if content exist
            if (!contentDisplay.isEmpty()) messageAttachmentsPretty.append(" ");

            // Attachment names
            messageAttachmentsPretty
                    .append("{")
                    .append(object.message.attachments.stream()
                            .map(attachment -> attachment.name != null ? attachment.name : "unknown")
                            .reduce((a, b) -> a + ", " + b)
                            .orElse(""))
                    .append("}");
        }

        // Get format and plugin placeholders
        final boolean hasEvent = object.event != null;
        final boolean hasChannel = object.channel != null;
        final String channelName = hasChannel && object.channel.name != null ? object.channel.name : "";
        Component message = EventAlertsIntegration.MINI_MESSAGE.deserialize(plugin.config.syncing.discord_to_minecraft.messages.format,
                Placeholder.unparsed("event_id", hasEvent && object.event.id != null ? object.event.id.toHexString() : ""),
                Placeholder.unparsed("event_type", hasEvent && object.event.type != null ? object.event.type : ""),
                Placeholder.unparsed("event_channel", hasEvent && object.event.channel != null ? object.event.channel.toString() : ""),
                Placeholder.unparsed("event_message", hasEvent && object.event.message != null ? object.event.message.toString() : ""),
                Placeholder.unparsed("event_control_panel", hasEvent && object.event.controlPanel != null ? object.event.controlPanel.toString() : ""),
                Placeholder.unparsed("event_custom", hasEvent && object.event.custom != null ? object.event.custom.toString() : ""),
                Placeholder.unparsed("event_created", hasEvent && object.event.created != null ? String.valueOf(object.event.created.getTime()) : ""),
                Placeholder.unparsed("event_title", hasEvent && object.event.title != null ? object.event.title : channelName),
                Placeholder.unparsed("event_host", hasEvent && object.event.host != null ? object.event.host.toString() : ""),
                Placeholder.unparsed("event_description", hasEvent && object.event.description != null ? object.event.description : ""),
                Placeholder.unparsed("event_roles", !hasEvent || object.event.roles == null ? "" : object.event.roles.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")),
                Placeholder.unparsed("event_roles_named", !hasEvent || object.event.rolesNamed == null ? "" : object.event.rolesNamed.stream()
                        .map(role -> role.name)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")),
                Placeholder.unparsed("event_server", hasEvent && object.event.server != null ? object.event.server.toHexString() : ""),
                Placeholder.unparsed("event_media_name", hasEvent && object.event.media != null && object.event.media.name != null ? object.event.media.name : ""),
                Placeholder.unparsed("event_ip", hasEvent && object.event.ip != null ? object.event.ip : ""),
                Placeholder.unparsed("event_platform", hasEvent ? Event.Platform.toString(object.event.platforms) : ""),
                Placeholder.unparsed("event_version", hasEvent && object.event.version != null ? object.event.version : ""),
                Placeholder.unparsed("event_prize", hasEvent && object.event.prize != null ? object.event.prize : ""),
                Placeholder.unparsed("event_max_players", hasEvent && object.event.maxPlayers != null ? object.event.maxPlayers.toString() : ""),
                Placeholder.unparsed("event_time", hasEvent && object.event.time != null ? String.valueOf(object.event.time.getTime()) : ""),
                Placeholder.unparsed("event_subscribers", !hasEvent || object.event.subscribers == null ? "" : object.event.subscribers.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")),
                Placeholder.unparsed("channel_id", hasChannel && object.channel.id != null ? object.channel.id.toString() : ""),
                Placeholder.unparsed("channel_name", channelName),
                Placeholder.unparsed("author_id", object.author != null && object.author.id != null ? object.author.id.toString() : ""),
                Placeholder.unparsed("author_name", object.author != null && object.author.name != null ? object.author.name : ""),
                Placeholder.unparsed("author_effectivename", object.author != null && object.author.effectiveName != null ? object.author.effectiveName : ""),
                Placeholder.unparsed("message_id", hasMessage && object.message.id != null ? object.message.id.toString() : ""),
                Placeholder.unparsed("message_content_raw", contentRaw),
                Placeholder.unparsed("message_content_display", contentDisplay),
                Placeholder.unparsed("message_content_stripped", contentStripped),
                Placeholder.unparsed("message_attachments_count", hasMessageAttachments ? String.valueOf(object.message.attachments.size()) : "0"),
                Placeholder.component("message_attachments_pretty", hasMessageAttachments ? Component.text(messageAttachmentsPretty.toString()).decorate(TextDecoration.ITALIC) : Component.empty()),
                Placeholder.unparsed("player_name", player != null ? player.getName() : ""));

        // PlaceholderAPI
        if (plugin.papiInstalled) message = PAPIComponents.setPlaceholders(player, message);

        // Send message
        Bukkit.broadcast(message);
    }
}
