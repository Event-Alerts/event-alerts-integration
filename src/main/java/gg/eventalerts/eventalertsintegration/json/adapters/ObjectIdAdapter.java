package gg.eventalerts.eventalertsintegration.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class ObjectIdAdapter extends TypeAdapter<ObjectId> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable ObjectId value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toHexString());
        }
    }

    @Override @Nullable
    public ObjectId read(@NotNull JsonReader in) {
        final String next = GSONProvider.getNextString(in);
        if (next != null) try {
            return new ObjectId(next);
        } catch (final Exception ignored) {}
        return null;
    }
}
