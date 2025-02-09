package gg.eventalerts.eventalertsintegration.utility;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.file.PlayableSound;


public class EventMessageUtility {
    @Nullable private static final TextColor SLIGHT_YELLOW = TextColor.fromCSSHexString("#f5f4c1");
    @Nullable private static final TextColor YELLOW = TextColor.fromCSSHexString("#f7e05c");

    @NotNull public static final TextComponent LINE = Component.text("\n│ ", NamedTextColor.DARK_GRAY);
    @NotNull public static final TextComponent BEGINNING = Component.text("┌──────────────────────", NamedTextColor.DARK_GRAY);
    @NotNull public static final TextComponent END = Component.text()
            .append(LINE)
            .append(LINE)
            .append(Component.text("ᴍᴏʀᴇ ɪɴꜰᴏ ᴀᴛ ", SLIGHT_YELLOW))
            .append(Component.text("ᴇᴠᴇɴᴛᴀʟᴇʀᴛꜱ.ɢɢ", YELLOW, TextDecoration.BOLD))
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
        return Component.text(content)
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/transfer " + ipPort.ip + " " + ipPort.port))
                .hoverEvent(Component.text()
                        .append(Component.text("Click to join this event server!\n")
                                .color(NamedTextColor.GREEN))
                        .append(Component.text(ipPort.ip + (ipPort.port != 25565 ? ":" + ipPort.port : ""))
                                .color(NamedTextColor.GRAY))
                        .build());
    }

    public static void broadcast(@NotNull EventAlertsIntegration plugin, @NotNull TextComponent message) {
        final PlayableSound sound = plugin.config.eventMessages.sound;
        final boolean playSound = sound != null;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            // Message
            player.sendMessage(message);
            // Sound
            if (playSound) sound.play(player);
        }
    }
}
