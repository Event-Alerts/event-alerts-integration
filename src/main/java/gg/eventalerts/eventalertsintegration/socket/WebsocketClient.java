package gg.eventalerts.eventalertsintegration.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.objects.EAObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.parents.Annoyable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocketHandshakeException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.*;
import java.util.logging.Level;


public abstract class WebSocketClient<T extends EAObject> implements Annoyable, WebSocket.Listener {
    @NotNull private static final Gson GSON = new Gson();
    @NotNull private static final ByteBuffer PING = ByteBuffer.wrap(new byte[]{0});
    @NotNull private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

    @NotNull protected final EventAlertsIntegration plugin;
    @NotNull private final SocketEndpoint endpoint;
    @NotNull private final Class<T> clazz;
    @Nullable public WebSocket webSocket;
    @Nullable private ScheduledFuture<?> keepAlive;
    @Nullable private ScheduledFuture<?> retryTask;

    public WebSocketClient(@NotNull EventAlertsIntegration plugin, @NotNull SocketEndpoint endpoint, @NotNull Class<T> clazz) {
        this.plugin = plugin;
        this.endpoint = endpoint;
        this.clazz = clazz;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    public abstract boolean shouldConnect();

    public void connect() {
        if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.INFO, "Attempting to establish WebSocket connection for " + endpoint);
        HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(plugin.getSocketHost() + endpoint.name().toLowerCase()), this)
                .whenCompleteAsync((newSocket, throwable) -> {
                    if (throwable != null) {
                        final Throwable cause1 = throwable.getCause();
                        final Throwable cause2 = cause1 != null ? cause1.getCause() : null;

                        // API offline
                        if (cause2 instanceof ClosedChannelException) {
                            AnnoyingPlugin.log(Level.SEVERE, "\n&c&lFailed to establish " + endpoint + " socket connection!\n&cThis is likely due to the Event Alerts API being offline\n&cIf this is not expected, please contact the Event Alerts developers\n&c&oThe server will try to connect again in 15 minutes...");
                            retryConnection("API offline", 10L);
                            return;
                        }

                        // Invalid endpoint
                        if (cause1 instanceof WebSocketHandshakeException) {
                            AnnoyingPlugin.log(Level.SEVERE, "\n&c&lFailed to establish " + endpoint + " socket connection!\n&cThis is likely due to an invalid endpoint\n&c&oIf this is not expected, please contact the Event Alerts developers");
                            return;
                        }

                        // Other error
                        AnnoyingPlugin.log(Level.SEVERE, "Failed to establish WebSocket connection!", throwable);
                        retryConnection("Error thrown when establishing connection", null);
                        return;
                    }

                    webSocket = newSocket;
                    webSocket.request(1);
                    keepAlive = SCHEDULER.scheduleAtFixedRate(() -> {
                        if (newSocket.isInputClosed()) {
                            retryConnection("Keep-alive detected closed input", null);
                            return;
                        }
                        newSocket.sendPing(PING);
                    }, 0, 30, TimeUnit.SECONDS);

                    if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.INFO, endpoint + " socket connection established");
                });
    }

    public void retryConnection(@NotNull String reason, @Nullable Long retryDelay) {
        if (retryTask != null) return;

        // Get delay from config
        if (retryDelay == null) {
            retryDelay = plugin.config.advanced.websockets.retryDelay;
            if (retryDelay == null) return;
        }

        // Retry connection
        close(1001, "Retrying connection");
        retryTask = SCHEDULER.schedule(() -> {
            if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.WARNING, "Retrying websocket connection for " + endpoint + " with reason: " + reason);
            retryTask = null;
            connect();
        }, retryDelay, TimeUnit.MINUTES);
    }

    public void close(int code, @NotNull String reason) {
        if (webSocket != null) webSocket.sendClose(code, reason);
        closeTasks();
    }

    private void closeTasks() {
        if (keepAlive != null) {
            keepAlive.cancel(true);
            keepAlive = null;
        }
        if (retryTask != null) {
            retryTask.cancel(true);
            retryTask = null;
        }
        webSocket = null;
    }

    @Override
    public CompletionStage<?> onText(@NotNull WebSocket webSocket, @NotNull CharSequence data, boolean last) {
        // Get message
        final String message = data.toString();
        webSocket.request(1); // Request more messages

        // Parse JSON
        final JsonObject json;
        try {
            json = GSON.fromJson(message, JsonObject.class);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.WARNING, "Failed to parse JSON: " + message);
            return null;
        }

        // Create object
        final T object = EAObject.newObject(plugin, clazz, json);
        if (object == null) return null;

        // Handle
        handle(object);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(@NotNull WebSocket webSocket, int statusCode, @NotNull String reason) {
        closeTasks();
        if (statusCode == 1006) {
            retryConnection("Experienced abnormal closure", null);
            return null;
        }
        if (plugin.config.advanced.websockets.logs) AnnoyingPlugin.log(Level.INFO, endpoint.name() + " socket closed with status code " + statusCode + " and reason: " + reason);
        return null;
    }

    @Override
    public void onError(@NotNull WebSocket webSocket, @NotNull Throwable error) {
        retryConnection("Experienced an error! See nearby for details...", null);
        error.printStackTrace();
    }

    public abstract void handle(@NotNull T object);
}
