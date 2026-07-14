package gg.eventalerts.eventalertsintegration.config.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.stats.Statable;


public abstract class ApiKey implements Statable {
    @Nullable public final String key;

    public ApiKey(@Nullable String key) {
        if (key != null && !key.startsWith(getPrefix())) key = null;
        this.key = key;
    }

    public ApiKey() {
        this(null);
    }

    @NotNull
    public abstract String getPrefix();

    @Override @NotNull
    public Object toStatValue() {
        return key != null;
    }
}
