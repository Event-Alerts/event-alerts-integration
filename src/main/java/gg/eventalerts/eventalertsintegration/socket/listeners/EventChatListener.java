package gg.eventalerts.eventalertsintegration.socket.listeners;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.object.sdk.EventUtility;
import gg.eventalerts.sdk.object.EAEventThreadMessage;
import gg.eventalerts.sdk.websocket.handler.EventChatHandler;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import me.clip.placeholderapi.PAPIComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class EventChatListener extends EventChatHandler {
    @NotNull private final EventAlertsIntegration plugin;
    
    public EventChatListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldSubscribe() {
        return plugin.config.syncing.discord_to_minecraft.messages.enabled && (plugin.config.api_keys.player.key != null || plugin.config.api_keys.server.key != null);
    }

    @Override
    public void onMessage(@NotNull SocketEvent<EAEventThreadMessage> event) {
        final EAEventThreadMessage threadMessage = event.data;
        if (threadMessage == null) return;

        // Run on Bukkit primary thread
        plugin.runOnMainThread(() -> {
            // Get Minecraft player
            final Player player = threadMessage.author != null && threadMessage.author.player != null && threadMessage.author.player.minecraft != null && threadMessage.author.player.minecraft.uuid != null ? Bukkit.getPlayer(threadMessage.author.player.minecraft.uuid) : null;

            // Remove newlines from content
            final boolean hasMessage = threadMessage.message != null;
            final boolean hasMessageContent = hasMessage && threadMessage.message.content != null;
            final String contentRaw = !hasMessageContent || threadMessage.message.content.raw == null ? "" : threadMessage.message.content.raw
                    .replace("\n", " ")
                    .replace("\r", " ");
            final String contentDisplay = !hasMessageContent || threadMessage.message.content.display == null ? "" : threadMessage.message.content.display
                    .replace("\n", " ")
                    .replace("\r", " ");
            final String contentStripped = !hasMessageContent || threadMessage.message.content.stripped == null ? "" : threadMessage.message.content.stripped
                    .replace("\n", " ")
                    .replace("\r", " ");

            // Build message_attachments_pretty
            final StringBuilder messageAttachmentsPretty = new StringBuilder();
            final boolean hasMessageAttachments = hasMessage && threadMessage.message.attachments != null && !threadMessage.message.attachments.isEmpty();
            if (hasMessageAttachments) {
                // Trailing 0 if content exist
                if (!contentDisplay.isEmpty()) messageAttachmentsPretty.append(" ");

                // Attachment names
                messageAttachmentsPretty
                        .append("{")
                        .append(threadMessage.message.attachments.stream()
                                .map(attachment -> attachment.name != null ? attachment.name : "unknown")
                                .reduce((a, b) -> a + ", " + b)
                                .orElse(""))
                        .append("}");
            }

            // Get format and plugin placeholders
            final boolean hasEvent = threadMessage.event != null;
            final boolean hasChannel = threadMessage.channel != null;
            final String channelName = hasChannel && threadMessage.channel.name != null ? threadMessage.channel.name : "";
            Component message = EventAlertsIntegration.MINI_MESSAGE.deserialize(plugin.config.syncing.discord_to_minecraft.messages.format,
                    Placeholder.unparsed("event_id", hasEvent && threadMessage.event.id != null ? threadMessage.event.id.toHexString() : ""),
                    Placeholder.unparsed("event_type", hasEvent && threadMessage.event.type != null ? threadMessage.event.type.displayName : ""),
                    Placeholder.unparsed("event_channel", hasEvent && threadMessage.event.channel != null ? threadMessage.event.channel.toString() : ""),
                    Placeholder.unparsed("event_message", hasEvent && threadMessage.event.message != null ? threadMessage.event.message.toString() : ""),
                    Placeholder.unparsed("event_control_panel", hasEvent && threadMessage.event.controlPanel != null ? threadMessage.event.controlPanel.toString() : ""),
                    Placeholder.unparsed("event_custom", hasEvent && threadMessage.event.custom != null ? threadMessage.event.custom.toString() : ""),
                    Placeholder.unparsed("event_created", hasEvent && threadMessage.event.created != null ? String.valueOf(threadMessage.event.created.getTime()) : ""),
                    Placeholder.unparsed("event_title", hasEvent && threadMessage.event.title != null ? threadMessage.event.title : channelName),
                    Placeholder.unparsed("event_host", hasEvent && threadMessage.event.host != null ? threadMessage.event.host.toString() : ""),
                    Placeholder.unparsed("event_description", hasEvent && threadMessage.event.description != null ? threadMessage.event.description : ""),
                    Placeholder.unparsed("event_roles", !hasEvent || threadMessage.event.roles == null ? "" : threadMessage.event.roles.stream()
                            .map(String::valueOf)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("")),
                    Placeholder.unparsed("event_roles_named", !hasEvent || threadMessage.event.rolesNamed == null ? "" : threadMessage.event.rolesNamed.stream()
                            .map(role -> role.displayName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("")),
                    Placeholder.unparsed("event_server", hasEvent && threadMessage.event.server != null ? threadMessage.event.server.toHexString() : ""),
                    Placeholder.unparsed("event_media_name", hasEvent && threadMessage.event.media != null && threadMessage.event.media.name != null ? threadMessage.event.media.name : ""),
                    Placeholder.unparsed("event_ip", hasEvent && threadMessage.event.ip != null ? threadMessage.event.ip : ""),
                    Placeholder.unparsed("event_platform", hasEvent ? EventUtility.Platform.toString(threadMessage.event.platforms) : ""),
                    Placeholder.unparsed("event_version", hasEvent && threadMessage.event.version != null ? threadMessage.event.version : ""),
                    Placeholder.unparsed("event_prize", hasEvent && threadMessage.event.prize != null ? threadMessage.event.prize : ""),
                    Placeholder.unparsed("event_max_players", hasEvent && threadMessage.event.maxPlayers != null ? threadMessage.event.maxPlayers.toString() : ""),
                    Placeholder.unparsed("event_time", hasEvent && threadMessage.event.time != null ? String.valueOf(threadMessage.event.time.getTime()) : ""),
                    Placeholder.unparsed("event_subscribers", !hasEvent || threadMessage.event.subscribers == null ? "" : threadMessage.event.subscribers.stream()
                            .map(String::valueOf)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("")),
                    Placeholder.unparsed("channel_id", hasChannel && threadMessage.channel.id != null ? threadMessage.channel.id.toString() : ""),
                    Placeholder.unparsed("channel_name", channelName),
                    Placeholder.unparsed("author_id", threadMessage.author != null && threadMessage.author.id != null ? threadMessage.author.id.toString() : ""),
                    Placeholder.unparsed("author_name", threadMessage.author != null && threadMessage.author.name != null ? threadMessage.author.name : ""),
                    Placeholder.unparsed("author_effectivename", threadMessage.author != null && threadMessage.author.effectiveName != null ? threadMessage.author.effectiveName : ""),
                    Placeholder.unparsed("message_id", hasMessage && threadMessage.message.id != null ? threadMessage.message.id.toString() : ""),
                    Placeholder.unparsed("message_content_raw", contentRaw),
                    Placeholder.unparsed("message_content_display", contentDisplay),
                    Placeholder.unparsed("message_content_stripped", contentStripped),
                    Placeholder.unparsed("message_attachments_count", hasMessageAttachments ? String.valueOf(threadMessage.message.attachments.size()) : "0"),
                    Placeholder.component("message_attachments_pretty", hasMessageAttachments ? Component.text(messageAttachmentsPretty.toString()).decorate(TextDecoration.ITALIC) : Component.empty()),
                    Placeholder.unparsed("player_name", player != null ? player.getName() : ""));

            // PlaceholderAPI
            if (plugin.papiInstalled) message = PAPIComponents.setPlaceholders(player, message);

            // Send message
            Bukkit.broadcast(message);
        });
    }
}
