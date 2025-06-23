package gg.eventalerts.eventalertsintegration.listeners;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.config.HostFilter;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import gg.eventalerts.eventalertsintegration.gui.config.advanced.AdvancedGui;
import gg.eventalerts.eventalertsintegration.gui.config.advanced.WebsocketsGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.EventMessagesGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.HostFilterGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.sound.SoundGui;

import io.papermc.paper.event.player.AsyncChatEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;

import java.util.ArrayList;
import java.util.List;


public class ChatListener extends AnnoyingListener {
    @NotNull private final EventAlertsIntegration plugin;

    public ChatListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @EventHandler
    public void onAsyncChat(@NotNull AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final String inputKey = plugin.guiInput.get(player.getUniqueId());
        if (inputKey == null) return;

        event.setCancelled(true);
        final String message = ((TextComponent) event.message()).content().trim();

        // Sound
        if (inputKey.startsWith(ConfigYml.EventMessages.PATH_SOUND)) {
            // Cancel
            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage(Component.text("\nCancelled sound change\n", NamedTextColor.GREEN));
                reopenSoundGui(player);
                return;
            }

            final String soundInputKey = inputKey.substring(ConfigYml.EventMessages.PATH_SOUND.length() + 1);
            switch (soundInputKey) {
                case "sound" -> {
                    // Get sound
                    final Sound sound;
                    try {
                        sound = Sound.valueOf(message.toUpperCase());
                    } catch (final IllegalArgumentException e) {
                        player.sendMessage(Component.text()
                                .color(NamedTextColor.RED)
                                .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                                .append(Component.text(" is not a valid sound!\nSee "))
                                .append(Component.text("https://srnyx.com/docs/spigot/org/bukkit/Sound.html", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED)
                                        .hoverEvent(Component.text("Click to open a list of sounds", NamedTextColor.YELLOW))
                                        .clickEvent(ClickEvent.openUrl("https://srnyx.com/docs/spigot/org/bukkit/Sound.html")))
                                .append(Component.text(" for a list of sounds"))
                                .append(ConfigGui.CANCEL));
                        return;
                    }

                    // Set sound
                    if (plugin.config.eventMessages.sound != null) plugin.config.eventMessages.sound.sound = sound;
                    plugin.config.setSave(inputKey, message);

                    // Send message and reopen GUI
                    player.sendMessage(Component.text()
                            .color(NamedTextColor.GREEN)
                            .append(Component.text("\nSound set to "))
                            .append(Component.text(message, NamedTextColor.DARK_GREEN))
                            .append(Component.text("!\n")));
                    reopenSoundGui(player);
                    return;
                }

                case "volume" -> {
                    // Get volume
                    final float volume;
                    try {
                        volume = Float.parseFloat(message);
                    } catch (final NumberFormatException e) {
                        player.sendMessage(Component.text()
                                .append(Component.text()
                                        .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                                        .append(Component.text(" is not a valid float!", NamedTextColor.RED)))
                                .append(ConfigGui.CANCEL));
                        return;
                    }

                    // Set volume
                    if (plugin.config.eventMessages.sound != null) plugin.config.eventMessages.sound.volume = volume;
                    plugin.config.setSave(inputKey, volume);

                    // Send message and reopen GUI
                    player.sendMessage(Component.text()
                            .color(NamedTextColor.GREEN)
                            .append(Component.text("\nSound volume set to "))
                            .append(Component.text(volume + "", NamedTextColor.DARK_GREEN))
                            .append(Component.text("!\n")));
                    reopenSoundGui(player);
                    return;
                }

                case "pitch" -> {
                    // Get pitch
                    final float pitch;
                    try {
                        pitch = Float.parseFloat(message);
                    } catch (final NumberFormatException e) {
                        player.sendMessage(Component.text()
                                .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                                .append(Component.text(" is not a valid float!", NamedTextColor.RED))
                                .append(ConfigGui.CANCEL));
                        return;
                    }

                    // Set pitch
                    if (plugin.config.eventMessages.sound != null) plugin.config.eventMessages.sound.pitch = pitch;
                    plugin.config.setSave(inputKey, pitch);

                    // Send message and reopen GUI
                    player.sendMessage(Component.text()
                            .color(NamedTextColor.GREEN)
                            .append(Component.text("\nSound pitch set to "))
                            .append(Component.text(pitch + "", NamedTextColor.DARK_GREEN))
                            .append(Component.text("!\n")));
                    reopenSoundGui(player);
                    return;
                }

                default -> {
                    // Unrecognized sound input key
                    player.sendMessage(Component.text()
                            .append(Component.text("\nUnrecognized sound input key: ", NamedTextColor.RED))
                            .append(Component.text(soundInputKey, NamedTextColor.DARK_RED))
                            .append(ConfigGui.CANCEL));
                    return;
                }
            }
        }

        // Websockets retry delay
        if (inputKey.equals(ConfigYml.Advanced.Websockets.PATH_RETRY_DELAY)) {
            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage(Component.text("\nCancelled websockets retry delay change\n", NamedTextColor.GREEN));
                reopenWebsocketsRetryDelayGui(player);
                return;
            }

            // Get retry delay
            final int retryDelay;
            try {
                retryDelay = Integer.parseInt(message);
            } catch (final NumberFormatException e) {
                player.sendMessage(Component.text()
                        .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                        .append(Component.text(" is not a valid integer!", NamedTextColor.RED))
                        .append(ConfigGui.CANCEL));
                return;
            }
            if (retryDelay < 3 && retryDelay != -1) {
                player.sendMessage(Component.text()
                        .append(Component.text("\nRetry delay must be at least 3 minutes (or -1 to disable retries)!", NamedTextColor.RED))
                        .append(ConfigGui.CANCEL));
                return;
            }

            // Set retry delay
            plugin.config.advanced.websockets.retryDelay = retryDelay;
            plugin.config.setSave(ConfigYml.Advanced.Websockets.PATH_RETRY_DELAY, retryDelay);

            // Send message and reopen GUI
            if (retryDelay == -1) {
                player.sendMessage(Component.text("\nWebsocket retries disabled!\n", NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text()
                        .color(NamedTextColor.GREEN)
                        .append(Component.text("\nWebsockets retry delay set to "))
                        .append(Component.text(retryDelay + " minutes", NamedTextColor.DARK_GREEN))
                        .append(Component.text("!\n")));
            }
            reopenWebsocketsRetryDelayGui(player);
            return;
        }

        // Host filter addition
        final HostFilter hostFilter = MiscUtility.handleException(() -> HostFilter.valueOf(inputKey.toUpperCase())).orElse(null);
        if (hostFilter == null) return;

        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled " + hostFilter.lower + " host filter addition\n", NamedTextColor.GREEN));
            reopenHostFilterGui(player, hostFilter);
            return;
        }

        // Get ID
        if (message.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("\nYou must provide a " + hostFilter.lower + "'s " + hostFilter.idType + " ID to add to the host filter!\n", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }
        final String id = message.toLowerCase();
        if (!hostFilter.idValidator.apply(plugin, id)) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\nInvalid " + hostFilter.idType + " ID "))
                    .append(Component.text(id, NamedTextColor.DARK_RED))
                    .append(Component.text("!"))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Add to host filter
        if (!hostFilter.setGetter.apply(plugin.config).add(id)) {
            player.sendMessage(Component.text()
                    .append(Component.text("\nThis " + hostFilter.lower + " is already in the host filter!\n", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Save config
        final List<String> combined = new ArrayList<>(plugin.config.eventMessages.hostFilterServers);
        combined.addAll(plugin.config.eventMessages.hostFilterUsers);
        plugin.config.setSave(ConfigYml.EventMessages.PATH_HOST_FILTER, combined);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nAdded " + hostFilter.lower + " with " + hostFilter.idType + " ID "))
                .append(Component.text(id, NamedTextColor.DARK_GREEN))
                .append(Component.text(" to the host filter!\n")));
        reopenHostFilterGui(player, hostFilter);
    }

    private void reopenSoundGui(@NotNull Player player) {
        plugin.guiInput.remove(player.getUniqueId());
        new SoundGui(new EventMessagesGui(new ConfigGui(plugin, player))).open(true);
    }

    private void reopenWebsocketsRetryDelayGui(@NotNull Player player) {
        plugin.guiInput.remove(player.getUniqueId());
        new WebsocketsGui(new AdvancedGui(new ConfigGui(plugin, player))).open(true);
    }

    private void reopenHostFilterGui(@NotNull Player player, @NotNull HostFilter hostFilter) {
        plugin.guiInput.remove(player.getUniqueId());
        new HostFilterGui(new EventMessagesGui(new ConfigGui(plugin, player))).openHostFilterGui(hostFilter);
    }
}
