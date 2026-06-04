package gg.eventalerts.eventalertsintegration.gui.config.advanced;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;


public class WebsocketGui extends AdvancedGui {
    public WebsocketGui(@NotNull AdvancedGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final String retryDelayValue = "<green>" + ConfigYml.Advanced.Websocket.formatRetryDelay(plugin.config.advanced.websocket.retry_delay);
        return Gui.of(1)
                .title(Component.text("Websocket", NamedTextColor.DARK_RED))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.advanced.websocket.logs,
                        "Logs",
                        "Whether to log websocket\nconnection messages",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.advanced.websocket.logs;
                            plugin.config.advanced.websocket.setLogs(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(1, booleanItem(
                        plugin.config.advanced.websocket.retry,
                        "Retry",
                        "Whether to automatically reconnect\nthe websocket if it disconnects",
                        (player, context) -> {
                            final boolean newValue = !plugin.config.advanced.websocket.retry;
                            plugin.config.advanced.websocket.setRetry(newValue);
                            playDingSound(newValue);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(3, ItemBuilder.from(Material.CLOCK)
                        .name(unitalicize(Component.text("Retry Delay", NamedTextColor.GOLD)))
                        .lore(lore("Duration until the websocket attempts to\nreconnect after being disconnected\n\n<gray>Current value: " + retryDelayValue))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat messages
                            player.sendMessage(Component.text()
                                    .append(Component.text("\nType the new retry delay in chat (for example 5m, 30s, or 1h)!", NamedTextColor.GREEN))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), GuiInputType.WEBSOCKET_RETRY_DELAY);
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
