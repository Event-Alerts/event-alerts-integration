package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.EAPlayer;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import gg.eventalerts.eventalertsintegration.socket.WebSocketClient;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;


public class LinkClient extends WebSocketClient<EAPlayer> {
    public LinkClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.LINK, EAPlayer.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.linking.requireLink;
    }

    @Override
    public void handle(@NotNull EAPlayer object) {
        // Check status
        if (object.linkStatus != EAPlayer.LinkStatus.REMOVED || object.uuid == null) return;

        // Kick player
        final Player player = Bukkit.getPlayer(object.uuid);
        if (player != null) plugin.runOnMainThread(() -> player.kick(Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You're no longer linked to a Discord account!", NamedTextColor.RED))
                .build()));
    }
}
