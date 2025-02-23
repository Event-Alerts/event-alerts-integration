package gg.eventalerts.eventalertsintegration.utility;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.file.PlayableSound;

import static gg.eventalerts.eventalertsintegration.EventAlertsIntegration.MINI_MESSAGE;


public class EventMessageUtility {
    @NotNull public static final TextComponent LINE = Component.text("\n│ ", NamedTextColor.DARK_GRAY);
    @NotNull public static final TextComponent BEGINNING = Component.text("┌──────────────────────", NamedTextColor.DARK_GRAY);
    @NotNull public static final TextComponent END = Component.text()
            .append(LINE)
            .append(LINE)
            .append(MINI_MESSAGE.deserialize("<#f5f4c1>ᴍᴏʀᴇ ɪɴꜰᴏ ᴀᴛ <#f7e05c><b>ᴇᴠᴇɴᴛᴀʟᴇʀᴛꜱ.ɢɢ")
                    .hoverEvent(Component.text("Click to open the website!", NamedTextColor.GREEN))
                    .clickEvent(ClickEvent.openUrl("https://eventalerts.gg")))
            .append(Component.text("\n└──────────────────────", NamedTextColor.DARK_GRAY))
            .build();

    @NotNull
    public static TextComponent getJoinButton(@NotNull EAStringUtility.IpPort ipPort) {
        return Component.text()
                .append(LINE)
                .append(LINE)
                .append(getButtonComponent("┌───────┐", ipPort))
                .append(LINE)
                .append(getButtonComponent("│ ─  JOIN!  ─ │", ipPort))
                .append(LINE)
                .append(getButtonComponent("└───────┘", ipPort))
                .build();
    }

    @NotNull
    private static TextComponent getButtonComponent(@NotNull String content, @NotNull EAStringUtility.IpPort ipPort) {
        return Component.text(content, NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/eventalertsintegration:eventalerts transfer " + ipPort.ip + " " + ipPort.port))
                .hoverEvent(MINI_MESSAGE.deserialize("<green>Click to join this event server!\n<gray>" + ipPort.ip + (ipPort.port != 25565 ? ":" + ipPort.port : "")));
    }

    public static void broadcast(@NotNull EventAlertsIntegration plugin, @NotNull TextComponent message) {
        final PlayableSound sound = plugin.config.eventMessages.sound;
        final boolean playSound = plugin.config.eventMessages.soundEnabled && sound != null;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            // Message
            player.sendMessage(message);
            // Sound
            if (playSound) sound.play(player);
        }
    }
}
