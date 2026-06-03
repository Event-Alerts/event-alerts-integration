package gg.eventalerts.eventalertsintegration;

import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.config.serdes.PlayableSoundSerializer;
import gg.eventalerts.eventalertsintegration.socket.WebSockets;
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

import java.io.File;
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

    @NotNull public final ConfigYml config = new ConfigYml(this);
    public WebSockets webSockets;
    /**
     * [player UUID, input key ID]
     */
    @NotNull public final Map<UUID, String> guiInput = new HashMap<>();

    public EventAlertsIntegration() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(PluginPlatform.modrinth("DmjI2XpF")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(24443))
                .registrationOptions.automaticRegistration.packages(
                        "gg.eventalerts.eventalertsintegration.commands",
                        "gg.eventalerts.eventalertsintegration.listeners");

        // Load BSON
        libraryManager.loadLibrary(EALibrary.BSON);

        // Configure config
        config.configure(opt -> {
            opt.configurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit(), registry -> {
                registry.register(new PlayableSoundSerializer());
            });
            opt.bindFile(new File(getDataFolder(), "config.yml"));
            opt.removeOrphans(true);
        });
        config.saveDefaults();
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
        AnnoyingPlugin.LOGGER.setLevel(config.advanced.debug ? Level.FINE : Level.INFO);
        HttpUtility.DEBUG = config.advanced.debug;

        // Reconnect websockets
        if (webSockets == null) webSockets = new WebSockets(this);
        webSockets.reconnectAll("Plugin reload");
    }

    @Override
    public void disable() {
        webSockets.closeAll("Plugin disable");
    }

    @NotNull
    public String getApiHost() {
        return config.advanced.use_testing_api ? "http://localhost:8080/api/v1/" : "https://eventalerts.gg/api/v1/";
    }

    @NotNull
    public String getSocketHost() {
        return config.advanced.use_testing_api ? "ws://localhost:9090/api/v1/socket/" : "wss://eventalerts.gg/api/v1/socket/";
    }

    @NotNull
    public String getUserAgent() {
        return getName() + "/" + getDescription().getVersion() + " (MC/" + AnnoyingPlugin.MINECRAFT_VERSION + ")";
    }

    @NotNull
    public Map<String, String> getSocketHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", getUserAgent());
        if (config.api_keys.player != null) headers.put("X-Player-Key", config.api_keys.player);
        if (config.api_keys.server != null) headers.put("X-Server-Key", config.api_keys.server);
        return headers;
    }

    public void runOnMainThread(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }
}
