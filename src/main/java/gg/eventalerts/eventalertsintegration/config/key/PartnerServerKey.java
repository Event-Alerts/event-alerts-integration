package gg.eventalerts.eventalertsintegration.config.key;

import org.jetbrains.annotations.NotNull;


public class PartnerServerKey extends ApiKey {
    public PartnerServerKey(@NotNull String key) {
        super(key);
    }

    public PartnerServerKey() {
        super();
    }

    @Override @NotNull
    public String getPrefix() {
        return "EA.PartnerServer.1.";
    }
}
