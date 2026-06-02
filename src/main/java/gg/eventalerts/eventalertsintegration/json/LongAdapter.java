package gg.eventalerts.eventalertsintegration.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
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
    public Long read(@NotNull JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            final String value = in.nextString();
            try {
                return Long.parseLong(value);
            } catch (final NumberFormatException e) {
                throw new JsonParseException("Expected a long value but got: " + value, e);
            }
        }
    }
}
