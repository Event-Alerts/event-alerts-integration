package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.socket.WebSockets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;
import xyz.srnyx.annoyingapi.libs.javautilities.MapGenerator;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class EventAlertsIntegration extends AnnoyingPlugin {
    @NotNull public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    @NotNull public static final Map<Long, String> ID_MAPPINGS = MapGenerator.HASH_MAP.mapOf(
            List.of(314853603695394817L, 365630764244664320L, 242385234992037888L, 604377897662414854L, 267734235224211467L, 381890968971902976L, 468890330763231270L, 1111741660892762142L, 1006349851241480242L, 1216096556713906288L, 1280002787446493256L, 1096205843113967669L, 533985117589471233L),
            List.of("Skeppy", "Oiiink", "srnyx", "hailey", "bacca", "Rame", "hayech", "Server Events", "Famous Events", "Potential Skeppy Events", "Skeppy Sighting", "Random Pings", "Reece"));
    @NotNull public static final TextComponent GATE = Component.text("EVENT ALERTS GATE\n\n", NamedTextColor.GOLD, TextDecoration.BOLD);
    @NotNull public static final TextComponent LINKING_INSTRUCTIONS = Component.text()
            .append(Component.text("Join the ", NamedTextColor.GRAY))
            .append(Component.text("eventalerts.gg", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.openUrl("https://eventalerts.gg")))
            .append(Component.text(" Discord and run ", NamedTextColor.GRAY))
            .append(Component.text("/linking help", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.copyToClipboard("/linking help")))
            .append(Component.text(" for more information", NamedTextColor.GRAY))
            .build();


    @NotNull public ConfigYml config;
    public WebSockets webSockets;

    public EventAlertsIntegration() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(new PluginPlatform.Multi(
                        PluginPlatform.modrinth("DmjI2XpF"),
                        PluginPlatform.hangar(this, "EventAlerts"))))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(24443))
                .registrationOptions.automaticRegistration.packages(
                        "gg.eventalerts.eventalertsintegration.commands",
                        "gg.eventalerts.eventalertsintegration.listeners");

        reload();
    }

    @Override
    public void reload() {
        // Load config
        config = new ConfigYml(this);
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
        return config.advanced.useTestingApi ? "http://localhost:8080/api/v1/" : "https://eventalerts.gg/api/v1/";
    }

    @NotNull
    public String getSocketHost() {
        return config.advanced.useTestingApi ? "ws://localhost:9090/api/v1/socket/" : "wss://eventalerts.gg/api/v1/socket/";
    }

    @NotNull
    public String getUserAgent() {
        return getName() + "/" + getDescription().getVersion() + " (MC/" + AnnoyingPlugin.MINECRAFT_VERSION + ")";
    }

    public void runOnMainThread(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnum(@NotNull Class<T> enumClass, @Nullable String string) {
        if (string != null) try {
            return Enum.valueOf(enumClass, string.toUpperCase());
        } catch (final IllegalArgumentException e) {
            log(Level.WARNING, "Invalid " + enumClass.getSimpleName() + ": " + string);
        }
        return null;
    }
}
