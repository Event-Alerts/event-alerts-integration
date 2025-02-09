package gg.eventalerts.eventalertsintegration.socket;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.socket.clients.CrossBanClient;
import gg.eventalerts.eventalertsintegration.socket.clients.EventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.FamousEventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.LinkClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.util.logging.Level;


public enum SocketEndpoint {
    EVENT_POSTED(EventPostedClient.class),
    FAMOUS_EVENT_POSTED(FamousEventPostedClient.class),
    CROSS_BAN(CrossBanClient.class),
    LINK(LinkClient.class);

    @NotNull public final Class<? extends SocketClient<?>> clientClass;

    SocketEndpoint(@NotNull Class<? extends SocketClient<?>> clientClass) {
        this.clientClass = clientClass;
    }

    @Nullable
    public SocketClient<?> createClient(@NotNull EventAlertsIntegration plugin) {
        try {
            return clientClass.getConstructor(EventAlertsIntegration.class).newInstance(plugin);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to create socket client for " + this, e);
        }
        return null;
    }
}
