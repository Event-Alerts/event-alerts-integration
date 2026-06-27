package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.config.migration.C0001_Migrate_sound_to_nested_structure;
import gg.eventalerts.eventalertsintegration.config.migration.C0002_Migrate_negative_retry_delay;
import gg.eventalerts.eventalertsintegration.config.migration.C0003_Migrate_websockets_to_websocket;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
import gg.eventalerts.eventalertsintegration.library.EventAlertsIntegrationLibrary;
import gg.eventalerts.eventalertsintegration.messages.EAMessagesProvider;
import gg.eventalerts.eventalertsintegration.socket.listeners.CrossBanListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.EventChatListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.EventPostedListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.FamousEventPostedListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.LinkListener;
import gg.eventalerts.eventalertsintegration.stats.FastStats;
import gg.eventalerts.eventalertsintegration.stats.StatsCollector;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.websocket.EAWebSocket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.java_websocket.framing.CloseFrame;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;


public class EventAlertsIntegration extends AnnoyingPlugin {
    @NotNull public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    @NotNull public static final TextComponent GATE = Component.text("EVENT ALERTS GATE\n\n", NamedTextColor.GOLD, TextDecoration.BOLD);
    @NotNull public static final TextComponent LINKING_INSTRUCTIONS = Component.text()
            .color(NamedTextColor.GRAY)
            .append(Component.text("Join the "))
            .append(Component.text("eventalerts.gg", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.openUrl("https://eventalerts.gg")))
            .append(Component.text(" Discord and run "))
            .append(Component.text("/linking help", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.copyToClipboard("/linking help")))
            .append(Component.text(" for more information"))
            .build();

    public ConfigYml config;
    public EAHTTP http;
    public EAWebSocket webSocket;
    @NotNull public final StatsCollector statsCollector = new StatsCollector();
    /**
     * [player UUID, pending GUI input target]
     */
    @NotNull public final Map<UUID, GuiInputType> guiInput = new HashMap<>();

    public EventAlertsIntegration() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.libraries(
                        EventAlertsIntegrationLibrary.EVENT_ALERTS_SDK_HTTP,
                        EventAlertsIntegrationLibrary.EVENT_ALERTS_SDK_WEBSOCKET,
                        EventAlertsIntegrationLibrary.TRIUMPH_GUI_PAPER))
                .statsOptions(statsOptions -> statsOptions
                        .bStats(bStatsOptions -> bStatsOptions.id(24443))
                        .fastStats(fastStatsOptions -> fastStatsOptions.loader(FastStats.class)));
    }

    @Override
    public void load() {
        // Build config
        config = configLoader.build(builder -> builder
                .config(new ConfigYml(this))
                .internalStateMigrations(
                        new C0001_Migrate_sound_to_nested_structure(),
                        new C0002_Migrate_negative_retry_delay(),
                        new C0003_Migrate_websockets_to_websocket()));

        loadReloadTasks();
    }

    @Override
    public void reload() {
        // Reload config
        config.load(true);

        loadReloadTasks();
    }

    private void loadReloadTasks() {
        // Toggle debug
        setDebug(config.advanced.debug);

        // Load SDK
        loadSDK();
    }

    @Override
    public void disable() {
        if (webSocket != null) webSocket.close(CloseFrame.NORMAL, "Plugin disable");
    }

    @Override @NotNull
    public EAMessagesProvider getMessages() {
        return (EAMessagesProvider) super.getMessages();
    }

    public void setDebug(boolean debug) {
        AnnoyingPlugin.LOGGER.setLevel(debug ? Level.FINE : Level.INFO);
        HttpUtility.DEBUG = debug;
    }

    public void loadSDK() {
        final String userAgent = getName() + "/" + getDescription().getVersion() + " (MC/" + AnnoyingPlugin.MINECRAFT_VERSION + ")";

        // Load HTTP
        http = new EAHTTP.Builder(userAgent)
                .url(config.advanced.use_testing_api ? "http://localhost:8080/api/v1/" : "https://eventalerts.gg/api/v1/")
                .playerKey(config.api_keys.getPlayer())
                .serverKey(config.api_keys.getServer())
                .build();

        // Load WebSocket
        if (webSocket != null) try {
            webSocket.closeBlocking();
        } catch (final InterruptedException e) {
            log(Level.WARNING, "Failed to close WebSocket", e);
        }
        webSocket = new EAWebSocket.Builder(userAgent)
                .url(config.advanced.use_testing_api ? "ws://localhost:9090/api/v1/socket" : "wss://eventalerts.gg/api/v1/socket")
                .handler(new CrossBanListener(this))
                .handler(new EventChatListener(this))
                .handler(new EventPostedListener(this))
                .handler(new FamousEventPostedListener(this))
                .handler(new LinkListener(this))
                .retry(config.advanced.websocket.retry)
                .retryDelay(config.advanced.websocket.retry_delay)
                .playerKey(config.api_keys.getPlayer())
                .serverKey(config.api_keys.getServer())
                .buildThenConnect();
    }

    public void runOnMainThread(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            scheduler.runSync(runnable);
        }
    }
}
