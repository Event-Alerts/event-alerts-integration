package gg.eventalerts.eventalertsintegration.config.key;

import org.jetbrains.annotations.NotNull;


public class PlayerKey extends ApiKey {
    public PlayerKey(@NotNull String key) {
        super(key);
    }

    public PlayerKey() {
        super();
    }

    @Override @NotNull
    public String getPrefix() {
        return "EA.Player.1.";
    }
}
