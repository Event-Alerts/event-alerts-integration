package gg.eventalerts.eventalertsintegration.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.EAObject;

import org.java_websocket.client.WebSocketClient;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.net.URI;
import java.util.concurrent.*;
import java.util.logging.Level;


public abstract class SocketClient<T extends EAObject> extends WebSocketClient {
    @NotNull private static final Gson GSON = new Gson();
    @NotNull private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

    @NotNull protected final EventAlertsIntegration plugin;
    @NotNull public final SocketEndpoint endpoint;
    @NotNull private final Class<T> objectClass;
    @Nullable private ScheduledFuture<?> retryTask;
    @Nullable public Runnable toRunOnStop;

    public SocketClient(@NotNull EventAlertsIntegration plugin, @NotNull SocketEndpoint endpoint, @NotNull Class<T> objectClass) {
        super(URI.create(plugin.getSocketHost() + endpoint.name().toLowerCase()));
        this.plugin = plugin;
        this.endpoint = endpoint;
        this.objectClass = objectClass;
    }

    public abstract boolean shouldConnect();

    public void open() {
        if (getReadyState() == ReadyState.NOT_YET_CONNECTED) {
            connect();
            return;
        }
        reconnect();
    }

    public void close(int code, @NotNull String reason, @Nullable Runnable toRunOnStop) {
        if (getReadyState() == ReadyState.NOT_YET_CONNECTED) {
            if (toRunOnStop != null) toRunOnStop.run();
            return;
        }
        this.toRunOnStop = toRunOnStop;
        close(code, reason);
    }

    public void retryConnection(@NotNull String reason, @Nullable Long retryDelay) {
        if (retryTask != null) return;

        // Get delay from config
        if (retryDelay == null) {
            retryDelay = plugin.config.advanced.websockets.retryDelay;
            if (retryDelay == null) return;
        }
        final Long finalRetryDelay = retryDelay;

        // Close connection
        close(1001, "Retrying connection");

        // Schedule retry
        retryTask = SCHEDULER.schedule(() -> {
            if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.WARNING, "Retrying websocket connection for " + endpoint + " with reason: " + reason);
            retryTask = null;
            connect();
        }, finalRetryDelay, TimeUnit.MINUTES);
    }

    @Override
    public void onOpen(@NotNull ServerHandshake handshake) {
        if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.INFO, endpoint.name() + " socket opened");
    }

    @Override
    public void onMessage(@NotNull String message) {
        // Parse JSON
        final JsonObject json;
        try {
            json = GSON.fromJson(message, JsonObject.class);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.WARNING, "Failed to parse JSON: " + message);
            return;
        }

        // Create object
        final T object = EAObject.newObject(plugin, objectClass, json);
        if (object == null) return;

        // Handle
        handle(object);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (retryTask != null) {
            retryTask.cancel(true);
            retryTask = null;
        }

        if (code == 1006){
            retryConnection("Experienced abnormal closure", null);
            return;
        }
        if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.INFO, endpoint.name() + " socket closed with status code " + code + " and reason: " + reason);

        if (toRunOnStop != null) {
            toRunOnStop.run();
            toRunOnStop = null;
        }
    }

    @Override
    public void onError(@NotNull Exception exception) {
        retryConnection("Experienced an error! See nearby for details...", null);
        exception.printStackTrace();
    }

    public abstract void handle(@NotNull T object);
}
