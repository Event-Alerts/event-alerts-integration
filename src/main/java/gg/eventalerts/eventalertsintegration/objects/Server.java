package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Server extends EAObject {
    @Nullable public String name;

    public Server(@NotNull JsonObject json) {
        super(json);
        name = json.has("name") ? json.get("name").getAsString() : null;
    }
}
