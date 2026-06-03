package gg.eventalerts.eventalertsintegration.objects;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class EAPlayer extends EAObject {
    // Database + Websocket
    @Nullable public Discord discord;
    @Nullable public Minecraft minecraft;
    // Websocket
    @Nullable public LinkStatus linkStatus;

    public static class Discord extends EAObject {
        @Nullable public Long id;
    }

    public static class Minecraft extends EAObject {
        @Nullable public UUID uuid;
    }

    public enum LinkStatus {
        ADDED,
        REMOVED
    }
}
