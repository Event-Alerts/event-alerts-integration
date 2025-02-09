package gg.eventalerts.eventalertsintegration.socket;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class WebSockets {
    @NotNull private final EventAlertsIntegration plugin;
    @NotNull private final Map<SocketEndpoint, SocketClient<?>> clients = new HashMap<>();

    public WebSockets(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
        if (!plugin.libraryManager.isLoaded(EALibrary.JAVA_WEBSOCKET)) plugin.libraryManager.loadLibrary(EALibrary.JAVA_WEBSOCKET);
    }

    public void reconnectAll(@Nullable String reason) {
        if (reason == null) reason = "No reason provided";

        // Create new clients immediately if none exist
        if (clients.isEmpty()) {
            createNewClients();
            return;
        }

        // Disconnect old clients
        final AtomicInteger disconnected = new AtomicInteger();
        final int size = clients.size();
        for (final SocketClient<?> client : clients.values()) client.close(1001, reason, () -> {
            // Wait for all clients to disconnect before creating new ones
            if (disconnected.incrementAndGet() != size) return;
            createNewClients();
        });
        clients.clear();
    }

    public final void reconnect(@Nullable String reason, @NotNull SocketEndpoint... endpoints) {
        if (reason == null) reason = "No reason provided";
        for (final SocketEndpoint endpoint : endpoints) {
            final SocketClient<?> client = clients.get(endpoint);

            // Create new client immediately if none exists
            if (client == null) {
                createNewClient(endpoint);
                continue;
            }

            // Disconnect old client
            client.close(1001, reason, () -> createNewClient(endpoint)); // Wait for disconnect before creating new
            clients.remove(endpoint);
        }
    }

    private void createNewClients() {
        for (final SocketEndpoint endpoint : SocketEndpoint.values()) createNewClient(endpoint);
    }

    private void createNewClient(@NotNull SocketEndpoint endpoint) {
        final SocketClient<?> client = endpoint.createClient(plugin);
        if (client == null || !client.shouldConnect()) return;
        clients.put(endpoint, client);
        client.connect();
    }

    public void closeAll(@Nullable String reason) {
        if (reason == null) reason = "No reason provided";
        for (final SocketClient<?> client : clients.values()) client.close(1001, reason);
        clients.clear();
    }

    public final void close(@Nullable String reason, @NotNull SocketEndpoint... endpoints) {
        if (reason == null) reason = "No reason provided";
        for (final SocketEndpoint endpoint : endpoints) {
            final SocketClient<?> client = clients.get(endpoint);
            if (client == null) continue;
            client.close(1001, reason);
            clients.remove(endpoint);
        }
    }
}
