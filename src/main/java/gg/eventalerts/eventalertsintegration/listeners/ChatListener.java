package gg.eventalerts.eventalertsintegration.listeners;

import com.cryptomorin.xseries.XSound;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.config.HostFilter;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
import gg.eventalerts.eventalertsintegration.gui.EAGui;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import gg.eventalerts.eventalertsintegration.gui.config.advanced.AdvancedGui;
import gg.eventalerts.eventalertsintegration.gui.config.advanced.WebsocketGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.EventMessagesGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.HostFilterGui;
import gg.eventalerts.eventalertsintegration.gui.config.eventmessages.sound.SoundGui;
import gg.eventalerts.eventalertsintegration.gui.config.syncing.SyncingGui;
import gg.eventalerts.eventalertsintegration.gui.config.syncing.discordtominecraft.MessagesGui;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingListener;

import java.time.Duration;


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
        final GuiInputType inputType = plugin.guiInput.get(player.getUniqueId());
        if (inputType == null) return;

        event.setCancelled(true);
        final String message = ((TextComponent) event.message()).content().trim();

        switch (inputType) {
            case EVENT_SOUND_ID -> handleSoundId(player, message);
            case EVENT_SOUND_VOLUME -> handleSoundVolume(player, message);
            case EVENT_SOUND_PITCH -> handleSoundPitch(player, message);
            case SYNC_MESSAGE_FORMAT -> handleFormat(player, message);
            case WEBSOCKET_RETRY_DELAY -> handleRetryDelay(player, message);
            case HOST_FILTER_ENTRY -> handleHostFilter(player, message);
        }
    }

    private void handleSoundId(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled sound change\n", NamedTextColor.GREEN));
            reopenSoundGui(player);
            return;
        }

        // Get sound
        final XSound sound = XSound.of(message).orElse(null);
        if (sound == null) {
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
        plugin.config.event_messages.sound.setSound(sound);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nSound set to "))
                .append(Component.text(sound.name(), NamedTextColor.DARK_GREEN))
                .append(Component.text("!\n")));
        reopenSoundGui(player);
    }

    private void handleSoundVolume(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled sound volume change\n", NamedTextColor.GREEN));
            reopenSoundGui(player);
            return;
        }

        // Get volume
        final float volume;
        try {
            volume = Float.parseFloat(message);
        } catch (final NumberFormatException e) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                    .append(Component.text(" is not a valid float!", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Set volume
        plugin.config.event_messages.sound.setVolume(volume);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nSound volume set to "))
                .append(Component.text(Float.toString(volume), NamedTextColor.DARK_GREEN))
                .append(Component.text("!\n")));
        reopenSoundGui(player);
    }

    private void handleSoundPitch(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled sound pitch change\n", NamedTextColor.GREEN));
            reopenSoundGui(player);
            return;
        }

        // Get pitch
        final float pitch;
        try {
            pitch = Float.parseFloat(message);
        } catch (final NumberFormatException e) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                    .append(Component.text(" is not a valid float!", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Set pitch
        plugin.config.event_messages.sound.setPitch(pitch);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nSound pitch set to "))
                .append(Component.text(Float.toString(pitch), NamedTextColor.DARK_GREEN))
                .append(Component.text("!\n")));
        reopenSoundGui(player);
    }

    private void handleFormat(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled format change\n", NamedTextColor.GREEN));
            reopenMessagesSyncingGui(player);
            return;
        }

        // Set format
        plugin.config.syncing.discord_to_minecraft.messages.setFormat(message);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nFormat set to "))
                .append(Component.text(message, NamedTextColor.DARK_GREEN))
                .append(Component.text("!\n")));
        reopenMessagesSyncingGui(player);
    }

    private void handleRetryDelay(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled websockets retry delay change\n", NamedTextColor.GREEN));
            reopenWebsocketsRetryDelayGui(player);
            return;
        }

        // Get duration
        final Duration retryDelay = (Duration) plugin.config.getConfigurer().getRegistry()
                .getTransformer(
                        GenericsDeclaration.of(String.class),
                        GenericsDeclaration.of(Duration.class))
                .transform(message, SerdesContext.of(plugin.config.getConfigurer()));
        if (retryDelay == null) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                    .append(Component.text(" is not a valid duration!", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Check if above minimum
        if (retryDelay.compareTo(ConfigYml.Advanced.Websocket.RETRY_DELAY_MIN) < 0) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\n" + message, NamedTextColor.DARK_RED))
                    .append(Component.text(" is below the minimum of "))
                    .append(Component.text(ConfigYml.Advanced.Websocket.formatRetryDelay(ConfigYml.Advanced.Websocket.RETRY_DELAY_MIN), NamedTextColor.DARK_RED))
                    .append(Component.text("!"))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Set retry delay
        plugin.config.advanced.websocket.setRetryDelay(retryDelay);

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nWebsockets retry delay set to "))
                .append(Component.text(message, NamedTextColor.DARK_GREEN))
                .append(Component.text("!\n")));
        reopenWebsocketsRetryDelayGui(player);
    }

    private void handleHostFilter(@NotNull Player player, @NotNull String message) {
        // Cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("\nCancelled host filter addition\n", NamedTextColor.GREEN));
            reopenHostFilterGui(player);
            return;
        }

        // Check if ID is valid
        final String id = message.toLowerCase();
        final HostFilter hostFilter = HostFilter.fromId(id);
        if (hostFilter == null) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\nInvalid host filter ID "))
                    .append(Component.text(id, NamedTextColor.DARK_RED))
                    .append(Component.text("!"))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Add to filter
        if (!plugin.config.event_messages.addHostFilter(id)) {
            player.sendMessage(Component.text()
                    .color(NamedTextColor.RED)
                    .append(Component.text("\nThis " + hostFilter.lower + " is already in the host filter!", NamedTextColor.RED))
                    .append(ConfigGui.CANCEL));
            return;
        }

        // Send message and reopen GUI
        player.sendMessage(Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("\nAdded " + hostFilter.lower + " with " + hostFilter.idType + " ID "))
                .append(Component.text(id, NamedTextColor.DARK_GREEN))
                .append(Component.text(" to the host filter!\n")));
        reopenHostFilterGui(player);
    }

    private void removeInput(@NotNull Player player) {
        plugin.guiInput.remove(player.getUniqueId());
    }

    private void reopenGui(@NotNull Player player, @NotNull EAGui gui) {
        removeInput(player);
        gui.open(true);
    }

    private void reopenSoundGui(@NotNull Player player) {
        reopenGui(player, new SoundGui(new EventMessagesGui(new ConfigGui(plugin, player))));
    }

    private void reopenMessagesSyncingGui(@NotNull Player player) {
        reopenGui(player, new MessagesGui(new SyncingGui(new ConfigGui(plugin, player))));
    }

    private void reopenWebsocketsRetryDelayGui(@NotNull Player player) {
        reopenGui(player, new WebsocketGui(new AdvancedGui(new ConfigGui(plugin, player))));
    }

    private void reopenHostFilterGui(@NotNull Player player) {
        reopenGui(player, new HostFilterGui(new EventMessagesGui(new ConfigGui(plugin, player))));
    }
}
