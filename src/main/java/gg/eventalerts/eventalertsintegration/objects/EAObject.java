package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;


public abstract class EAObject {
    @NotNull public final JsonObject json;

    public EAObject(@NotNull JsonObject json) {
        this.json = json;
    }

    @NotNull Set<Long> toLongSet(@NotNull JsonArray array) {
        final Set<Long> set = new HashSet<>();
        for (final JsonElement element : array) set.add(element.getAsLong());
        return set;
    }

    @Nullable
    public static <T> T newObject(@NotNull EventAlertsIntegration plugin, @NotNull Class<T> clazz, @NotNull JsonObject json) {
        // Install BSON if needed
        if (clazz == Event.class) plugin.libraryManager.loadLibrary(EALibrary.BSON);

        // Return new object from JSON
        try {
            return clazz.getConstructor(JsonObject.class).newInstance(json);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to parse " + clazz.getSimpleName() + " from JSON: " + json, e);
            return null;
        }
    }
}
