package gg.eventalerts.eventalertsintegration.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.objects.EAObject;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;

import java.util.logging.Level;


public class JoinListener extends AnnoyingListener {
    @NotNull private final EventAlertsIntegration plugin;

    public JoinListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @EventHandler
    public void onPlayerLogin(@NotNull PlayerLoginEvent event) {
        if (checkLinking(event)) checkCrossBan(event);
    }

    private boolean checkLinking(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.linking.requireLink) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.linking.bypass")) return true;

        // Make API request
        JsonElement json = null;
        try {
            json = HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players/minecraft/uuid/" + player.getUniqueId()).orElse(null);
        } catch (final Exception ignored) {}
        if (json == null) {
            failLinking(event, "Failed to get JSON response");
            return false;
        }
        final JsonObject root = json.getAsJsonObject();

        // Check code
        final int code = root.get("code").getAsInt();
        if (code != 200) {
            failLinking(event, "Invalid code: " + code);
            return false;
        }

        // Get player
        final JsonElement playerElement = root.get("player");
        if (playerElement == null) {
            failLinking(event, "Failed to get player");
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

    private void failLinking(@NotNull PlayerLoginEvent event, @NotNull String reason) {
        AnnoyingPlugin.log(Level.SEVERE, "Failed to check linking status for " + event.getPlayer().getName() + ": " + reason);
        if (!plugin.config.linking.allowJoinOnFailure) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("Failed to check linking status, try again later!\n\n", NamedTextColor.RED))
                .append(Component.text("If this issue persists, contact support", NamedTextColor.GRAY))
                .build());
    }

    private boolean checkCrossBan(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.crossBan.enabled) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.crossban.bypass")) return true;

        // Make API request
        JsonElement json = null;
        try {
            json = HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "cross_bans/uuid/" + player.getUniqueId()).orElse(null);
        } catch (final Exception ignored) {}
        if (json == null) {
            failCrossBan(event, "Failed to get JSON response");
            return false;
        }
        final JsonObject root = json.getAsJsonObject();

        // Check code
        final int code = root.get("code").getAsInt();
        if (code != 200) {
            failCrossBan(event, "Invalid code: " + code);
            return false;
        }

        // Get ban
        final JsonElement ban = root.get("cross_ban");
        if (ban == null || ban.isJsonNull()) return true;
        final CrossBan crossBan = EAObject.newObject(plugin, CrossBan.class, ban.getAsJsonObject());
        if (crossBan == null) {
            failCrossBan(event, "Failed to parse CrossBan");
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

    private void failCrossBan(@NotNull PlayerLoginEvent event, @NotNull String reason) {
        AnnoyingPlugin.log(Level.SEVERE, "Failed to check cross-ban status for " + event.getPlayer().getName() + ": " + reason);
        if (!plugin.config.crossBan.allowJoinOnFailure) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("Failed to check cross-ban status, try again later!\n\n", NamedTextColor.RED))
                .append(Component.text("If this issue persists, contact support", NamedTextColor.GRAY))
                .build());
    }
}
