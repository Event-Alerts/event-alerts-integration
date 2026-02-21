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
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;

import java.util.UUID;
import java.util.logging.Level;

import static gg.eventalerts.eventalertsintegration.EventAlertsIntegration.MINI_MESSAGE;


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
