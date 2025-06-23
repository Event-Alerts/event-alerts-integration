package gg.eventalerts.eventalertsintegration.gui.config.eventmessages.sound;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.EventMessagesGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;

import org.jetbrains.annotations.NotNull;


public class SoundGui extends EventMessagesGui {
    public SoundGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("Sound"))
                .statelessComponent(container -> container.setItem(0,
                        booleanItem(
                                plugin.config.eventMessages.soundEnabled,
                                "Enabled",
                                "Whether to play a sound when\nan event message is broadcasted",
                                (player, context) -> {
                                    final boolean newStatus = !plugin.config.eventMessages.soundEnabled;
                                    plugin.config.eventMessages.soundEnabled = newStatus;
                                    plugin.config.setSave(ConfigYml.EventMessages.PATH_SOUND_ENABLED, newStatus);
                                    playDingSound(newStatus);
                                    open(false);
                                })))
                .statelessComponent(container -> container.setItem(1, ItemBuilder.from(Material.NOTE_BLOCK)
                        .name(uninitialize(Component.text("Sound", NamedTextColor.GOLD)))
                        .lore(lore("The ID/name of the sound that will play\n\n<gray>Current value: " + (plugin.config.eventMessages.sound == null
                                ? "<red>Disabled"
                                : "<green>" + plugin.config.eventMessages.sound.sound)))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat message
                            player.sendMessage(Component.text()
                                    .color(NamedTextColor.GREEN)
                                    .append(Component.text("\nType the new sound ID in chat!\nSee "))
                                    .append(Component.text("https://srnyx.com/docs/spigot/org/bukkit/Sound.html", NamedTextColor.DARK_GREEN, TextDecoration.UNDERLINED)
                                            .hoverEvent(Component.text("Click to open a list of sounds", NamedTextColor.YELLOW))
                                            .clickEvent(ClickEvent.openUrl("https://srnyx.com/docs/spigot/org/bukkit/Sound.html")))
                                    .append(Component.text(" for a list of sounds"))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), ConfigYml.EventMessages.PATH_SOUND + ".sound");
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(2, ItemBuilder.from(Material.BELL)
                        .name(uninitialize(Component.text("Volume", NamedTextColor.GOLD)))
                        .lore(lore("The volume at which the\nsound will be played\n\n<gray>Current value: " + (plugin.config.eventMessages.sound == null
                                ? "<red>Disabled"
                                : "<green>" + plugin.config.eventMessages.sound.volume)))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat message
                            player.sendMessage(Component.text()
                                    .append(Component.text("\nType the new sound volume in chat!", NamedTextColor.GREEN))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), ConfigYml.EventMessages.PATH_SOUND + ".volume");
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(3, ItemBuilder.from(Material.AMETHYST_SHARD)
                        .name(uninitialize(Component.text("Pitch", NamedTextColor.GOLD)))
                        .lore(lore("The pitch at which the\nsound will be played\n\n<gray>Current value: " + (plugin.config.eventMessages.sound == null
                                ? "<red>Disabled"
                                : "<green>" + plugin.config.eventMessages.sound.pitch)))
                        .asGuiItem((player, context) -> { //TODO switch to anvil GUI when Triumph GUI updates
                            // Send chat message
                            player.sendMessage(Component.text()
                                    .append(Component.text("\nType the new sound pitch in chat!", NamedTextColor.GREEN))
                                    .append(CANCEL));

                            // Add to map and close GUI
                            plugin.guiInput.put(player.getUniqueId(), ConfigYml.EventMessages.PATH_SOUND + ".pitch");
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(4, ItemBuilder.from(Material.NAME_TAG)
                        .name(uninitialize(Component.text("Category", NamedTextColor.GOLD)))
                        .lore(lore("The category that the sound\nwill be played through\n\n<gray>Current value: " + (plugin.config.eventMessages.sound == null
                                ? "<red>Disabled"
                                : "<green>" + plugin.config.eventMessages.sound.category)))
                        .asGuiItem((player, context) -> new CategoryGui(this).open(true))))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
