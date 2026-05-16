package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;


public abstract class EAObject {
    @NotNull
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        for (final Field field : getClass().getDeclaredFields()) {
            // Skip static fields
            if (Modifier.isStatic(field.getModifiers())) continue;

            // Add field to JSON
            try {
                boolean inaccessible = !field.canAccess(this);
                if (inaccessible) field.setAccessible(true);
                final Object value = field.get(this);
                if (inaccessible) field.setAccessible(false);
                if (value == null) continue;
                final String name = field.getName();
                if (value instanceof EAObject eaObject) {
                    json.add(name, eaObject.toJson());
                    continue;
                }
                json.add(name, EventAlertsIntegration.GSON.toJsonTree(value));
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    @Override @NotNull
    public String toString() {
        return toJson().toString();
    }

    /**
     * {@code clazz} must have a constructor that takes a {@link JsonObject} as its only parameter
     */
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
