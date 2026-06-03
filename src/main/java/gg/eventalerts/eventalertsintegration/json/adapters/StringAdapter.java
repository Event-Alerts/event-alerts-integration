package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class StringAdapter extends TypeAdapter<String> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable String value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override @Nullable
    public String read(@NotNull JsonReader in) {
        return GSONProvider.getNextString(in);
    }
}
