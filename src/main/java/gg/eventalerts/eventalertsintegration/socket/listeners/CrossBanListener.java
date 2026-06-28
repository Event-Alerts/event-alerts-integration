package gg.eventalerts.eventalertsintegration.socket.listeners;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.object.sdk.CrossBanUtility;
import gg.eventalerts.sdk.websocket.handler.CrossBanHandler;
import gg.eventalerts.sdk.websocket.message.event.EACrossBanEvent;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;


public class CrossBanListener extends CrossBanHandler {
    @NotNull private final EventAlertsIntegration plugin;

    public CrossBanListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldSubscribe() {
        return plugin.config.cross_ban.enabled;
    }

    @Override
    public void onMessage(@NotNull SocketEvent<EACrossBanEvent> event) {
        final EACrossBanEvent crossBan = event.data;
        if (crossBan == null) return;

        // Check status
        if (crossBan.status != EACrossBanEvent.Status.ADDED) return;

        // Kick player
        if (crossBan.minecraftUuid == null) return;
        final Player player = Bukkit.getPlayer(crossBan.minecraftUuid);
        if (player != null) plugin.runOnMainThread(() -> player.kick(Component.text()
                        .append(EventAlertsIntegration.GATE)
                        .append(Component.text("You have been cross-banned from all event servers!", NamedTextColor.RED))
                        .append(CrossBanUtility.getReasonExpires(crossBan))
                        .build(),
                PlayerKickEvent.Cause.BANNED));
    }
}
