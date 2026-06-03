package gg.eventalerts.eventalertsintegration.objects;

import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.jetbrains.annotations.NotNull;


public abstract class EAObject {
    @Override @NotNull
    public String toString() {
        return GSONProvider.GSON.toJson(this);
    }
}
