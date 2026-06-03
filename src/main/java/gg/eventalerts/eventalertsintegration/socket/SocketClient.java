package gg.eventalerts.eventalertsintegration.socket;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.scheduler.TaskWrapper;

import java.net.URI;
import java.time.Duration;
import java.util.logging.Level;


public abstract class SocketClient<T extends EAObject> extends WebSocketClient {
    @NotNull protected final EventAlertsIntegration plugin;
    @NotNull public final SocketEndpoint endpoint;
    @NotNull private final Class<T> objectClass;
    @Nullable private TaskWrapper retryTask;
    @Nullable public Runnable toRunOnStop;

    public SocketClient(@NotNull EventAlertsIntegration plugin, @NotNull SocketEndpoint endpoint, @NotNull Class<T> objectClass) {
        super(URI.create(plugin.getSocketHost() + endpoint.name().toLowerCase()), plugin.getSocketHeaders());
        this.plugin = plugin;
        this.endpoint = endpoint;
        this.objectClass = objectClass;
    }

    public void close(int code, @NotNull String reason, @Nullable Runnable toRunOnStop) {
        final ReadyState readyState = getReadyState();
        if (readyState == ReadyState.NOT_YET_CONNECTED || readyState == ReadyState.CLOSED) {
            if (toRunOnStop != null) toRunOnStop.run();
            return;
        }
        this.toRunOnStop = toRunOnStop;
        close(code, reason);
    }

    public void retryConnection(@NotNull String reason, @Nullable Duration retryDelay) {
        if (retryTask != null || !plugin.config.advanced.websocket.retry) return;

        // Get delay from config if not specified
        if (retryDelay == null) retryDelay = plugin.config.advanced.websocket.retry_delay;

        // Close connection
        close(1001, "Retrying connection");

        // Schedule retry
        if (plugin.config.advanced.websocket.logs) AnnoyingPlugin.log(Level.INFO, "We will try to reconnect to " + endpoint + " in " + retryDelay + " minutes due to: " + reason);
        retryTask = plugin.scheduler.runGlobalTaskLaterAsync(task -> {
            if (plugin.config.advanced.websocket.logs) AnnoyingPlugin.log(Level.INFO, "Retrying websocket connection for " + endpoint + " with reason: " + reason);
            retryTask = null;
            reconnect();
        }, retryDelay.toMillis() / 50);
    }

    public void send(@NotNull T object) {
        send(GSONProvider.GSON.toJson(object));
    }

    @Override
    public void onOpen(@NotNull ServerHandshake handshake) {
        if (plugin.config.advanced.websocket.logs) AnnoyingPlugin.log(Level.INFO, endpoint.name() + " socket opened");
    }

    @Override
    public void onMessage(@NotNull String message) {
        // Create object
        final T object;
        try {
            object = GSONProvider.GSON.fromJson(message, objectClass);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.WARNING, "Failed to parse JSON: " + message);
            return;
        }
        if (object == null) return;

        // Handle
        onMessage(object);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Protect against double-errors when initial connection fails
        if (code == CloseFrame.NEVER_CONNECTED) return;

        // Cancel retryTask
        if (retryTask != null) {
            retryTask.cancel();
            retryTask = null;
        }

        // Abnormal closure
        if (code == CloseFrame.ABNORMAL_CLOSE) {
            retryConnection("Experienced abnormal closure", null);
            return;
        }

        // Log closure
        if (plugin.config.advanced.websocket.logs) AnnoyingPlugin.log(Level.INFO, endpoint.name() + " socket closed with status code " + code + " and reason: " + reason);

        // Run toRunOnStop
        if (toRunOnStop != null) {
            toRunOnStop.run();
            toRunOnStop = null;
        }
    }

    @Override
    public void onError(@NotNull Exception exception) {
        retryConnection("Experienced an error! If logs are enabled, see nearby for details.", null);
        if (plugin.config.advanced.websocket.logs) exception.printStackTrace();
    }

    public abstract boolean shouldConnect();

    public void onMessage(@NotNull T object) {}
}
