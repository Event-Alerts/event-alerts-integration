package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.DiscordSRVHook;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.EventThreadMessage;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.libs.javautilities.StringUtility;


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

        // DiscordSRV //TODO no idea if this works
        if (plugin.config.discordMessageSyncing.discordSRVIntegration && Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            DiscordSRVHook.broadcastMessageToMinecraftServer(object.content.display);
            return;
        }

        // Format
        String message = plugin.config.discordMessageSyncing.format;

        // PlaceholderAPI
        if (plugin.papiInstalled) message = PlaceholderAPI.setPlaceholders(player, message);

        // Plugin placeholders
        message = message
                .replace("{messageId}", object.messageId)
                .replace("{eventId}", object.eventId)
                .replace("{channel_id}", object.channel.id)
                .replace("{channel_name}", object.channel.name)
                .replace("{author_id}", object.author.id)
                .replace("{author_name}", object.author.name)
                .replace("{author_effectiveName}", object.author.effectiveName)
                .replace("{content_raw}", object.content.raw)
                .replace("{content_display}", object.content.display)
                .replace("{content_stripped}", object.content.stripped);

        // Remove newlines
        message = message
                .replace("\n", " ")
                .replace("\r", " ");

        Bukkit.broadcast(EventAlertsIntegration.MINI_MESSAGE.deserialize(StringUtility.shorten(message, 256)));
    }
}
