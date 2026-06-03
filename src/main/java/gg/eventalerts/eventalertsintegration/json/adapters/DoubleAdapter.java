package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class DoubleAdapter extends TypeAdapter<Double> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable Double value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override @Nullable
    public Double read(@NotNull JsonReader in) {
        final JsonToken token = GSONProvider.peek(in);
        if (token == null) return null;

        final double next;
        try {
            next = in.nextDouble();
        } catch (final Exception e) {
            try {
                in.skipValue();
            } catch (final Exception ignored) {}
            return null;
        }

        return next;
    }
}
