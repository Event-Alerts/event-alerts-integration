package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.gui.EAGui;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.sound.SoundGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;

import org.jetbrains.annotations.NotNull;


public class EventMessagesGui extends ConfigGui {
    public EventMessagesGui(@NotNull EAGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(1)
                .title(Component.text("Event Messages"))
                .statelessComponent(container -> container.setItem(0, booleanItem(
                        plugin.config.eventMessages.enabled,
                        "Enabled",
                        "Whether to enable event messages\nbeing broadcast in the server chat",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.eventMessages.enabled;
                            plugin.config.eventMessages.setEnabled(newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(1, booleanItem(
                        plugin.config.eventMessages.detectIps,
                        "Detect IPs <dark_gray>1.20.5+",
                        "If an IP is detected in an event message,\nplayers will be able to click a button to join\nthe event's server using transfer packets",
                        (player, context) -> {
                            final boolean newStatus = !plugin.config.eventMessages.detectIps;
                            plugin.config.eventMessages.detectIps = newStatus;
                            plugin.config.setSave(ConfigYml.EventMessages.PATH_DETECT_IPS, newStatus);
                            playDingSound(newStatus);
                            open(false);
                        })))
                .statelessComponent(container -> container.setItem(3, ItemBuilder.from(Material.NOTE_BLOCK)
                        .name(unitalicize(Component.text("SOUND", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("The sound to play when an\nevent message is broadcasted"))
                        .asGuiItem((player, context) -> new SoundGui(this).open(true))))
                .statelessComponent(container -> container.setItem(4, ItemBuilder.from(Material.NAME_TAG)
                        .name(unitalicize(Component.text("IGNORED TYPES", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Types of events that shouldn't\nbe broadcasted in the server chat"))
                        .asGuiItem((player, context) -> new IgnoredTypesGui(this).open(true))))
                .statelessComponent(container -> container.setItem(5, ItemBuilder.from(Material.EMERALD)
                        .name(unitalicize(Component.text("IGNORED PARTNER ROLES", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Ignore Partner events that\nmention any of these roles"))
                        .asGuiItem((player, context) -> new IgnoredPartnerRolesGui(this).open(true))))
                .statelessComponent(container -> container.setItem(6, ItemBuilder.from(Material.PLAYER_HEAD)
                        .name(unitalicize(Component.text("HOST FILTER", NamedTextColor.GOLD, TextDecoration.BOLD)))
                        .lore(lore("Only broadcast events\nfrom these specific hosts"))
                        .asGuiItem((player, context) -> new HostFilterGui(this).open(true))))
                .statelessComponent(container -> container.setItem(8, backButton()));
    }
}
