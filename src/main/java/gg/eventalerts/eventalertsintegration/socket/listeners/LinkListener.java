package gg.eventalerts.eventalertsintegration.socket.listeners;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.sdk.websocket.handler.LinkHandler;
import gg.eventalerts.sdk.websocket.message.event.EALinkEvent;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class LinkListener extends LinkHandler {
    @NotNull private final EventAlertsIntegration plugin;

    public LinkListener(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldSubscribe() {
        return plugin.config.linking.require_link;
    }

    @Override
    public void onMessage(@NotNull SocketEvent<EALinkEvent> event) {
        final EALinkEvent link = event.data;
        if (link == null || link.minecraft == null || link.minecraft.uuid == null) return;

        // Linked
        if (link.linkStatus == EALinkEvent.LinkStatus.ADDED && link.discord != null) return;

        // Unlinked
        if (link.linkStatus != EALinkEvent.LinkStatus.REMOVED || !plugin.config.linking.require_link) return;
        // Kick player
        final Player player = Bukkit.getPlayer(link.minecraft.uuid);
        if (player != null) plugin.runOnMainThread(() -> player.kick(Component.text()
                .append(EventAlertsIntegration.GATE)
                .append(Component.text("You're no longer linked to a Discord account!", NamedTextColor.RED))
                .build()));
    }
}
