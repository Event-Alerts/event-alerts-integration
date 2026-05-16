package gg.eventalerts.eventalertsintegration.gui.config.syncing;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.paper.container.type.HopperContainerType;

import gg.eventalerts.eventalertsintegration.gui.EAGui;
import gg.eventalerts.eventalertsintegration.gui.Heads;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import gg.eventalerts.eventalertsintegration.gui.config.syncing.discordtominecraft.MessagesGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.jetbrains.annotations.NotNull;


public class SyncingGui extends ConfigGui {
    public SyncingGui(@NotNull EAGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("Syncing"))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.syncing.minecraftToDiscord.connections,
                        "Minecraft -> Discord Connections",
                        "Whether to enable join/quit messages\nbeing synced to Discord threads",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.syncing.minecraftToDiscord.connections;
                            plugin.config.syncing.minecraftToDiscord.setConnections(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.skull()
                        .texture(Heads.BLUE_ARROW_DOWN)
                        .name(unitalicize(Component.text("DISCORD -> MINECRAFT MESSAGES", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Manage syncing settings for\nDiscord -> Minecraft messages"))
                        .asGuiItem((player, context) -> new MessagesGui(this).open(true))))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }
}
