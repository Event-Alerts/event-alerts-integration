package gg.eventalerts.eventalertsintegration.gui.config.syncing.discordtominecraft;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
import gg.eventalerts.eventalertsintegration.gui.config.syncing.SyncingGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;


public class MessagesGui extends SyncingGui {
    public MessagesGui(@NotNull SyncingGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("Discord -> Minecraft Messages"))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.syncing.discord_to_minecraft.messages.enabled,
                        "Enabled",
                        "Whether to enable Discord messages\nbeing synced to Minecraft chat",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.syncing.discord_to_minecraft.messages.enabled;
                            plugin.config.syncing.discord_to_minecraft.messages.setEnabled(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.from(Material.WRITABLE_BOOK)
                        .name(unitalicize(Component.text("Format", NamedTextColor.GOLD)))
                        .lore(lore("The format of Discord messages in the Minecraft chat.\nSee config.yml for placeholders!\n\n<gray>Current value: <reset>" + plugin.config.syncing.discord_to_minecraft.messages.format))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat message
                            player.sendMessage(Component.text()
                                    .color(NamedTextColor.GREEN)
                                    .append(Component.text("\nType the new message format in chat!\nSee config.yml for placeholders\nClick "))
                                    .append(Component.text("here", NamedTextColor.DARK_GREEN, TextDecoration.UNDERLINED)
                                            .hoverEvent(Component.text(plugin.config.syncing.discord_to_minecraft.messages.format, NamedTextColor.YELLOW))
                                            .clickEvent(ClickEvent.suggestCommand(plugin.config.syncing.discord_to_minecraft.messages.format)))
                                    .append(Component.text(" to insert the current format to chat"))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), GuiInputType.SYNC_MESSAGE_FORMAT);
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
