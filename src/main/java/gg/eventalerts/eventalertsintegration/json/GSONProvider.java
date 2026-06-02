package gg.eventalerts.eventalertsintegration.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Date;


public final class GSONProvider {
    @NotNull public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Long.class, new LongAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
            .create();
}
