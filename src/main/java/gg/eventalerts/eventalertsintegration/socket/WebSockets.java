package gg.eventalerts.eventalertsintegration.socket;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.socket.clients.CrossBanClient;
import gg.eventalerts.eventalertsintegration.socket.clients.EventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.FamousEventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.LinkClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class WebSockets {
    @NotNull private final Set<WebSocketClient<?>> clients;

    public WebSockets(@NotNull EventAlertsIntegration plugin) {
        clients = Set.of(
                new FamousEventPostedClient(plugin),
                new EventPostedClient(plugin),
                new CrossBanClient(plugin),
                new LinkClient(plugin));
    }

    public void connectAll() {
        for (final WebSocketClient<?> client : clients) if (client.shouldConnect()) client.connect();
    }

    @SafeVarargs
    public final void connect(@NotNull Class<? extends WebSocketClient<?>>... classes) {
        final Set<Class<? extends WebSocketClient<?>>> classSet = Set.of(classes);
        for (final WebSocketClient<?> client : clients) if (classSet.contains(client.getClass()) && client.shouldConnect()) client.connect();
    }

    public void closeAll(@Nullable String reason) {
        if (reason == null) reason = "No reason provided";
        for (final WebSocketClient<?> client : clients) client.close(1001, reason);
    }

    @SafeVarargs
    public final void close(@Nullable String reason, @NotNull Class<? extends WebSocketClient<?>>... classes) {
        if (reason == null) reason = "No reason provided";
        final Set<Class<? extends WebSocketClient<?>>> classSet = Set.of(classes);
        for (final WebSocketClient<?> client : clients) if (classSet.contains(client.getClass())) client.close(1001, reason);
    }
}
