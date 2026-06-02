package gg.eventalerts.eventalertsintegration.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class ObjectIdAdapter extends TypeAdapter<ObjectId> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull ObjectId value) throws IOException {
        out.value(value.toHexString());
    }

    @Override @NotNull
    public ObjectId read(@NotNull JsonReader in) throws IOException {
        return new ObjectId(in.nextString());
    }
}
