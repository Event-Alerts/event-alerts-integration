package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.util.logging.Level;


public abstract class EAObject {
    @NotNull public final JsonObject json;

    public EAObject(@NotNull JsonObject json) {
        this.json = json;
    }

    @Nullable
    public static <T extends EAObject> T newObject(@NotNull EventAlertsIntegration plugin, @NotNull Class<T> clazz, @NotNull JsonObject json) {
        // Install BSON if needed
        if (clazz == Event.class && !plugin.libraryManager.isLoaded(EALibrary.BSON)) plugin.libraryManager.loadLibrary(EALibrary.BSON);

        // Return new object from JSON
        try {
            return clazz.getConstructor(JsonObject.class).newInstance(json);
        } catch (final Exception e) {
            AnnoyingPlugin.log(Level.SEVERE, "Failed to parse " + clazz.getSimpleName() + " from JSON: " + json, e);
            return null;
        }
    }
}
