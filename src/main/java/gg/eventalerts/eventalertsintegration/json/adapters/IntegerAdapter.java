package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class IntegerAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override @Nullable
    public Integer read(@NotNull JsonReader in) {
        final JsonToken token = GSONProvider.peek(in);
        if (token == null) return null;

        final int next;
        try {
            next = in.nextInt();
        } catch (final Exception e) {
            try {
                in.skipValue();
            } catch (final Exception ignored) {}
            return null;
        }

        return next;
    }
}
