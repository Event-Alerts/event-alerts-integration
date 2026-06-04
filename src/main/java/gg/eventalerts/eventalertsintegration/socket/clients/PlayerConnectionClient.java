package gg.eventalerts.eventalertsintegration.socket.clients;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.PlayerConnection;
import gg.eventalerts.eventalertsintegration.socket.SocketClient;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import org.jetbrains.annotations.NotNull;


public class PlayerConnectionClient extends SocketClient<PlayerConnection> {
    public PlayerConnectionClient(@NotNull EventAlertsIntegration plugin) {
        super(plugin, SocketEndpoint.PLAYER_CONNECTION, PlayerConnection.class);
    }

    @Override
    public boolean shouldConnect() {
        return plugin.config.syncing.minecraft_to_discord.connections && plugin.config.api_keys.getServer() != null;
    }
}
