package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class EnumAdapter implements TypeAdapterFactory {
    @Override @Nullable
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        final Class<? super T> rawType = type.getRawType();
        if (rawType == Enum.class || !Enum.class.isAssignableFrom(rawType)) return null;

        return new TypeAdapter<>() {
            @Override
            public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(((Enum<?>) value).name());
                }
            }

            @Override @Nullable
            public T read(@NotNull JsonReader in) {
                final String next = GSONProvider.getNextString(in);
                if (next != null) try {
                    return (T) Enum.valueOf((Class<? extends Enum>) rawType.asSubclass(Enum.class), next);
                } catch (final Exception ignored) {}
                return null;
            }
        };
    }
}
