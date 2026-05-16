package gg.eventalerts.eventalertsintegration.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import gg.eventalerts.eventalertsintegration.objects.PlayerConnection;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.clients.PlayerConnectionClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;

import java.util.UUID;
import java.util.logging.Level;

import static gg.eventalerts.eventalertsintegration.EventAlertsIntegration.MINI_MESSAGE;


public class PlayerListener extends AnnoyingListener {
    @NotNull private final EventAlertsIntegration plugin;

    public PlayerListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED || plugin.config.apiKeys.serverApiKey == null) return;

        // Get PlayerConnectionClient
        final SocketClient<?> client = plugin.webSockets.clients.get(SocketEndpoint.PLAYER_CONNECTION);
        if (client == null || !client.isOpen() || !(client instanceof PlayerConnectionClient connectionClient)) return;

        // Send JOIN message
        final Player player = event.getPlayer();
        connectionClient.send(new PlayerConnection(
                player.getUniqueId(),
                player.getName(),
                System.currentTimeMillis(),
                PlayerConnection.Type.JOIN));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        if (plugin.config.apiKeys.serverApiKey == null) return;

        // Get PlayerConnectionClient
        final SocketClient<?> client = plugin.webSockets.clients.get(SocketEndpoint.PLAYER_CONNECTION);
        if (client == null || !client.isOpen() || !(client instanceof PlayerConnectionClient connectionClient)) return;

        // Send QUIT message
        final Player player = event.getPlayer();
        connectionClient.send(new PlayerConnection(
                player.getUniqueId(),
                player.getName(),
                System.currentTimeMillis(),
                PlayerConnection.Type.QUIT));
    }

    @EventHandler
    public void onPlayerLogin(@NotNull PlayerLoginEvent event) {
        if (checkLinking(event)) checkCrossBan(event);
    }

    /**
     * @return  true if the player is allowed to join, false if they should be kicked (or if the check failed and they should be kicked or allowed based on config)
     */
    private boolean checkLinking(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.linking.requireLink || !plugin.config.linking.checkOnJoin) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.linking.bypass")) return true;
        final UUID uuid = player.getUniqueId();

        // Make API request
        final JsonObject json;
        try {
            json = HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players/minecraft/uuid/" + uuid, null)
                    .map(JsonElement::getAsJsonObject)
                    .orElse(null);
        } catch (final Exception e) {
            failLinking(event, "Exception", e);
            return false;
        }
        if (json == null) {
            failLinking(event, "Failed to get JSON response", null);
            return false;
        }

        // Check code
        final int code = json.get("code").getAsInt();
        if (code != 200) {
            failLinking(event, "Invalid code: " + code, null);
            return false;
        }

        // Get player
        final JsonElement playerElement = json.get("player");
        if (playerElement == null) {
            failLinking(event, "Failed to get player", null);
            return false;
        }

        // Disallow if not linked
        if (!playerElement.isJsonNull()) return true;
        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You must link your Minecraft account with Event Alerts to join this server!\n\n", NamedTextColor.RED))
                .append(EventAlertsIntegration.LINKING_INSTRUCTIONS)
                .build());
        return false;
    }

    private void failLinking(@NotNull PlayerLoginEvent event, @NotNull String reason, @Nullable Exception exception) {
        AnnoyingPlugin.log(Level.SEVERE, "Failed to check linking status for " + event.getPlayer().getName() + ": " + reason, exception);
        if (!plugin.config.linking.allowJoinOnFailure) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(MINI_MESSAGE.deserialize("<red>Failed to check linking status, try again later!\n\n<gray>If this issue persists, contact support"))
                .build());
    }

    /**
     * @return  true if the player is allowed to join, false if they should be kicked (or if the check failed and they should be kicked or allowed based on config)
     */
    private boolean checkCrossBan(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.crossBan.enabled || !plugin.config.crossBan.checkOnJoin) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.crossban.bypass")) return true;

        // Make API request
        final JsonObject json;
        try {
            json = HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "cross_bans/minecraft_uuid/" + player.getUniqueId(), null)
                    .map(JsonElement::getAsJsonObject)
                    .orElse(null);
        } catch (final Exception e) {
            failCrossBan(event, "Exception", e);
            return false;
        }
        if (json == null) {
            failCrossBan(event, "Failed to get JSON response", null);
            return false;
        }

        // Check code
        final int code = json.get("code").getAsInt();
        if (code != 200) {
            failCrossBan(event, "Invalid code: " + code, null);
            return false;
        }

        // Get ban
        final JsonElement ban = json.get("cross_ban");
        if (ban == null || ban.isJsonNull()) return true;
        final CrossBan crossBan = EAObject.newObject(plugin, CrossBan.class, ban.getAsJsonObject());
        if (crossBan == null) {
            failCrossBan(event, "Failed to parse CrossBan", null);
            return false;
        }

        // Check expiration
        if (crossBan.expiration != null && crossBan.expiration.getTime() < System.currentTimeMillis()) return true;

        // Kick
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You are cross-banned from all event servers!", NamedTextColor.RED))
                .append(crossBan.getReasonExpires())
                .build());
        return false;
    }

    private void failCrossBan(@NotNull PlayerLoginEvent event, @NotNull String reason, @Nullable Exception exception) {
        AnnoyingPlugin.log(Level.SEVERE, "Failed to check cross-ban status for " + event.getPlayer().getName() + ": " + reason, exception);
        if (!plugin.config.crossBan.allowJoinOnFailure) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(MINI_MESSAGE.deserialize("<red>Failed to check cross-ban status, try again later!\n\n<gray>If this issue persists, contact support"))
                .build());
    }
}
