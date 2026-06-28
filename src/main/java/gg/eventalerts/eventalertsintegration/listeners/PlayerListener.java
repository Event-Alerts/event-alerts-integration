package gg.eventalerts.eventalertsintegration.listeners;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.object.sdk.CrossBanUtility;
import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
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

import java.util.Date;
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
        if (!plugin.config.syncing.minecraft_to_discord.connections || event.getResult() != PlayerLoginEvent.Result.ALLOWED || plugin.config.api_keys.getServer() == null) return;

        // Send JOIN message
        final Player player = event.getPlayer();
        plugin.webSocket.send(SocketActionName.PLAYER_CONNECTION, new EAPlayerConnectionAction(
                player.getUniqueId(),
                player.getName(),
                new Date(),
                EAPlayerConnectionAction.Type.JOIN));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        if (!plugin.config.syncing.minecraft_to_discord.connections || plugin.config.api_keys.getServer() == null) return;

        // Send QUIT message
        final Player player = event.getPlayer();
        plugin.webSocket.send(SocketActionName.PLAYER_CONNECTION, new EAPlayerConnectionAction(
                player.getUniqueId(),
                player.getName(),
                new Date(),
                EAPlayerConnectionAction.Type.QUIT));
    }

    @EventHandler
    public void onPlayerLogin(@NotNull PlayerLoginEvent event) {
        if (checkLinking(event)) checkCrossBan(event);
    }

    /**
     * @return  true if the player is allowed to join, false if they should be kicked (or if the check failed and they should be kicked or allowed based on config)
     */
    private boolean checkLinking(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.linking.require_link || !plugin.config.linking.check_on_join) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.linking.bypass")) return true;
        final UUID uuid = player.getUniqueId();

        // Get player
        final EAPlayer eaPlayer;
        try {
            eaPlayer = plugin.http.players.retrieveOneByMinecraftUuid(uuid).complete();
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to check linking status for " + player.getName(), e);

            // Can join on failure
            if (plugin.config.linking.allow_join_on_failure) return true;

            // Can't join on failure
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                    .append(EventAlertsIntegration.GATE)
                    .append(MINI_MESSAGE.deserialize("<red>Failed to check linking status, try again later!\n\n<gray>If this issue persists, contact support"))
                    .build());
            return false;
        }

        // Is linked
        if (eaPlayer != null) return true;

        // Disallow if not linked
        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You must link your Minecraft account with Event Alerts to join this server!\n\n", NamedTextColor.RED))
                .append(EventAlertsIntegration.LINKING_INSTRUCTIONS)
                .build());
        return false;
    }

    /**
     * @return  true if the player is allowed to join, false if they should be kicked (or if the check failed and they should be kicked or allowed based on config)
     */
    private boolean checkCrossBan(@NotNull PlayerLoginEvent event) {
        if (!plugin.config.cross_ban.enabled || !plugin.config.cross_ban.check_on_join) return true;
        final Player player = event.getPlayer();

        // Check permission
        if (player.hasPermission("eventalerts.crossban.bypass")) return true;

        // Make request
        final EACrossBan crossBan;
        try {
            crossBan = plugin.http.crossBans.retrieveOneByMinecraftUuid(player.getUniqueId()).complete();
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to check cross-ban status for " + player.getName(), e);

            // Can join on failure
            if (plugin.config.cross_ban.allow_join_on_failure) return true;

            // Can't join on failure
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text()
                    .append(EventAlertsIntegration.GATE)
                    .append(MINI_MESSAGE.deserialize("<red>Failed to check cross-ban status, try again later!\n\n<gray>If this issue persists, contact support"))
                    .build());
            return false;
        }

        // Not cross-banned
        if (crossBan == null) return true;

        // Check expiration
        if (crossBan.expiration != null && crossBan.expiration.getTime() < System.currentTimeMillis()) return true;

        // Kick
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You are cross-banned from all event servers!", NamedTextColor.RED))
                .append(CrossBanUtility.getReasonExpires(crossBan))
                .build());
        return false;
    }
}
