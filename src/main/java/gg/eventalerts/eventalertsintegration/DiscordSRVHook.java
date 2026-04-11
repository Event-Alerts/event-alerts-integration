package gg.eventalerts.eventalertsintegration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;

import org.jetbrains.annotations.NotNull;


public class DiscordSRVHook {
    public static void broadcastMessageToMinecraftServer(@NotNull String message) {
        DiscordSRV.getPlugin().broadcastMessageToMinecraftServer("global", Component.text(message), null);
    }
}
