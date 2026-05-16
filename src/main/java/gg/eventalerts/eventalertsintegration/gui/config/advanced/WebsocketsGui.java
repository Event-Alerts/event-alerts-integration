package gg.eventalerts.eventalertsintegration.gui.config.advanced;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.paper.container.type.HopperContainerType;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Material;

import org.jetbrains.annotations.NotNull;


public class WebsocketsGui extends AdvancedGui {
    public WebsocketsGui(@NotNull AdvancedGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final String retryDelayValue = plugin.config.advanced.websockets.retryDelay == null
                ? "<red>Disabled"
                : "<green>" + plugin.config.advanced.websockets.retryDelay + " minutes";
        return Gui.of(new HopperContainerType())
                .title(Component.text("Websockets", NamedTextColor.DARK_RED))
                .statelessComponent(container -> container.setItem(0, ItemBuilder.from(Material.CLOCK)
                        .name(unitalicize(Component.text("Retry Delay", NamedTextColor.GOLD)))
                        .lore(lore("Minutes until a websocket attempts to\nreconnect after being disconnected\n\n<gray>Current value: " + retryDelayValue))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat messages
                            player.sendMessage(Component.text()
                                    .append(Component.text("\nType the new retry delay in chat (in minutes, minimum of 3, -1 to disable retries)!", NamedTextColor.GREEN))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), ConfigYml.Advanced.Websockets.PATH_RETRY_DELAY);
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(1, booleanItem(
                        plugin.config.advanced.websockets.logs,
                        "Logs",
                        "Whether to log websocket\nconnection messages",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.advanced.websockets.logs;
                            plugin.config.advanced.websockets.logs = newStatus;
                            plugin.config.setSave(ConfigYml.Advanced.Websockets.PATH_LOGS, newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
