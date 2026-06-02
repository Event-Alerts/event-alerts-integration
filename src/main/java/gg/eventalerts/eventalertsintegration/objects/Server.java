package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;


public class Server extends EAObject {
    @Nullable public String name;

    public Server(@NotNull JsonObject json) {
        name = json.has("name") ? MiscUtility.handleException(() -> json.get("name").getAsString()).orElse(null) : null;
    }
}
