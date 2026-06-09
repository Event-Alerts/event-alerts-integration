package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.config.ConfigCreator;
import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
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
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;
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

    @NotNull public final ConfigYml config;
    public EAHTTP http;
    public EAWebSocket webSocket;
    @NotNull public final StatsCollector statsCollector = new StatsCollector();
    /**
     * [player UUID, pending GUI input target]
     */
    @NotNull public final Map<UUID, GuiInputType> guiInput = new HashMap<>();

    public EventAlertsIntegration() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(PluginPlatform.modrinth("DmjI2XpF")))
                .statsOptions(statsOptions -> statsOptions
                        .bStats(bStatsOptions -> bStatsOptions.id(24443))
                        .fastStats(fastStatsOptions -> fastStatsOptions.loader(FastStats.class)))
                .registrationOptions.automaticRegistration.packages(
                        "gg.eventalerts.eventalertsintegration.commands",
                        "gg.eventalerts.eventalertsintegration.listeners");

        // Load libraries
        libraryManager.loadLibrary(EALibrary.EVENT_ALERTS_SDK_HTTP);
        libraryManager.loadLibrary(EALibrary.EVENT_ALERTS_SDK_WEBSOCKET);
        libraryManager.loadLibrary(EALibrary.OKAERI_CONFIGS_YAML_BUKKIT);
        libraryManager.loadLibrary(EALibrary.OKAERI_CONFIGS_SERDES_COMMONS);
        libraryManager.loadLibrary(EALibrary.OKAERI_CONFIGS_SERDES_BUKKIT);
        libraryManager.loadLibrary(EALibrary.OKAERI_CONFIGS_VALIDATOR_OKAERI);
        libraryManager.loadLibrary(EALibrary.TRIUMPH_GUI_PAPER);

        // Configure config
        config = ConfigCreator.create(this);
    }

    @Override
    public void enable() {
        reload(); // Websocket retry task requires plugin to be enabled
    }

    @Override
    public void reload() {
        // Load config
        config.load(true);

        // Toggle debug
        setDebug(config.advanced.debug);

        // Reload HTTP and websocket
        setupHTTP();
        setupWebSocket();
    }

    @Override
    public void disable() {
        if (webSocket != null) webSocket.close(1000, "Plugin disable");
    }

    public void setupHTTP() {
        http = new EAHTTP.Builder(getUserAgent())
                .url(getApiHost())
                .playerKey(config.api_keys.getPlayer())
                .serverKey(config.api_keys.getServer())
                .build();
    }

    public void setupWebSocket() {
        if (webSocket != null) webSocket.close(1000, "Plugin reload");
        webSocket = new EAWebSocket.Builder(getUserAgent())
                .url(getSocketHost())
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

    @NotNull
    public String getApiHost() {
        return config.advanced.use_testing_api ? "http://localhost:8080/api/v1/" : "https://eventalerts.gg/api/v1/";
    }

    @NotNull
    public String getSocketHost() {
        return config.advanced.use_testing_api ? "ws://localhost:9090/api/v1/socket" : "wss://eventalerts.gg/api/v1/socket";
    }

    @NotNull
    public String getUserAgent() {
        return getName() + "/" + getDescription().getVersion() + " (MC/" + AnnoyingPlugin.MINECRAFT_VERSION + ")";
    }

    public void setDebug(boolean debug) {
        AnnoyingPlugin.LOGGER.setLevel(debug ? Level.FINE : Level.INFO);
        HttpUtility.DEBUG = debug;
    }

    public void runOnMainThread(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            scheduler.runSync(runnable);
        }
    }
}
