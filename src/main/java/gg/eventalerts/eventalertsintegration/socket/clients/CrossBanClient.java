package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.WebSocketClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

import org.jetbrains.annotations.NotNull;


public class CrossBanClient extends WebSocketClient<CrossBan> {
    public CrossBanClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.CROSS_BAN, CrossBan.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.crossBan.enabled;
    }

    @Override
    public void handle(@NotNull CrossBan object) {
        // Check status
        if (object.status != CrossBan.Status.ADDED) return;

        // Kick player
        final Player player = Bukkit.getPlayer(object.uuid);
        if (player != null) plugin.runOnMainThread(() -> player.kick(Component.text()
                        .append(Component.text("EVENT ALERTS GATE\n\n", NamedTextColor.GOLD, TextDecoration.BOLD))
                        .append(Component.text("You have been cross-banned from all event servers!", NamedTextColor.RED))
                        .append(object.getReasonExpires())
                        .build(),
                PlayerKickEvent.Cause.BANNED));
    }
}
