package gg.eventalerts.eventalertsintegration.socket;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.socket.clients.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.lang.reflect.Constructor;
import java.util.logging.Level;


public enum SocketEndpoint {
    EVENT_POSTED(EventPostedClient.class),
    FAMOUS_EVENT_POSTED(FamousEventPostedClient.class),
    CROSS_BAN(CrossBanClient.class),
    LINK(LinkClient.class),
    EVENT_CHAT(EventChatClient.class);

    @Nullable public final Constructor<? extends SocketClient<?>> clientConstructor;

    SocketEndpoint(@NotNull Class<? extends SocketClient<?>> clientClass) {
        Constructor<? extends SocketClient<?>> constructor = null;
        try {
            constructor = clientClass.getConstructor(EventAlertsIntegration.class);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to get client constructor for " + this, e);
        }
        this.clientConstructor = constructor;
    }

    @Nullable
    public SocketClient<?> newClient(@NotNull EventAlertsIntegration plugin) {
        if (clientConstructor != null) try {
            return clientConstructor.newInstance(plugin);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to create socket client for " + this, e);
        }
        return null;
    }
}
