package gg.eventalerts.eventalertsintegration.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import gg.eventalerts.eventalertsintegration.json.adapters.*;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;


public final class GSONProvider {
    @NotNull public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Boolean.class, new BooleanAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .registerTypeAdapter(Double.class, new DoubleAdapter())
            .registerTypeAdapter(double.class, new DoubleAdapter())
            .registerTypeAdapterFactory(new EnumAdapter())
            .registerTypeAdapter(Integer.class, new IntegerAdapter())
            .registerTypeAdapter(int.class, new IntegerAdapter())
            .registerTypeAdapter(Long.class, new LongAdapter())
            .registerTypeAdapter(long.class, new LongAdapter())
            .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
            .registerTypeAdapter(String.class, new StringAdapter())
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
            .registerTypeAdapterFactory(new SetAdapter())
            .create();

    @Nullable
    public static JsonToken peek(@NotNull JsonReader in) {
        final JsonToken token;
        try {
            token = in.peek();
        } catch (final Exception e) {
            return null;
        }

        if (token == JsonToken.NULL) {
            try {
                in.nextNull();
            } catch (final Exception ignored) {}
            return null;
        }

        return token;
    }

    @Nullable
    public static String getNextString(@NotNull JsonReader in) {
        final JsonToken token = peek(in);
        if (token != null) try {
            return in.nextString();
        } catch (final Exception e) {
            try {
                in.skipValue();
            } catch (final Exception ignored) {}
        }
        return null;
    }
}
