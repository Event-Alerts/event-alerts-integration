package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;


public class UUIDAdapter extends TypeAdapter<UUID> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable UUID value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override @Nullable
    public UUID read(@NotNull JsonReader in) {
        final String next = GSONProvider.getNextString(in);
        if (next != null) try {
            return UUID.fromString(next);
        } catch (final Exception ignored) {}
        return null;
    }
}
