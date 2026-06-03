package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class LongAdapter extends TypeAdapter<Long> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable Long value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override @Nullable
    public Long read(@NotNull JsonReader in) {
        final String next = GSONProvider.getNextString(in);
        if (next != null) try {
            return Long.parseLong(next);
        } catch (final Exception ignored) {}
        return null;
    }
}
