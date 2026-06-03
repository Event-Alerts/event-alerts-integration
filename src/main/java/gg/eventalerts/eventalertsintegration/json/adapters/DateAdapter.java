package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Date;


public class DateAdapter extends TypeAdapter<Date> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(String.valueOf(value.getTime()));
        }
    }

    @Override @Nullable
    public Date read(@NotNull JsonReader in) {
        final String next = GSONProvider.getNextString(in);
        if (next != null) try {
            return new Date(Long.parseLong(next));
        } catch (final Exception ignored) {}
        return null;
    }
}