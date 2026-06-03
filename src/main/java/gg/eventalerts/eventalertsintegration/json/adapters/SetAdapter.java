package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;


public class SetAdapter implements TypeAdapterFactory {
    @Override @Nullable
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        if (!Set.class.isAssignableFrom(type.getRawType())) return null;
        if (!(type.getType() instanceof final ParameterizedType parameterizedType)) return null;

        final Type elementType = parameterizedType.getActualTypeArguments()[0];
        final TypeAdapter<Object> elementAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(elementType));

        return new TypeAdapter<>() {
            @Override
            public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }

                out.beginArray();
                for (final Object element : (Set<?>) value) elementAdapter.write(out, element);
                out.endArray();
            }

            @Override @Nullable
            public T read(@NotNull JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                final Set<Object> values = new LinkedHashSet<>();
                in.beginArray();
                while (in.hasNext()) {
                    final Object element = elementAdapter.read(in);
                    if (element != null) values.add(element);
                }
                in.endArray();

                return (T) values;
            }
        };
    }
}
