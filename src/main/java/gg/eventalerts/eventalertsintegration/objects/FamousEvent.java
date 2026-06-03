package gg.eventalerts.eventalertsintegration.objects;

import gg.eventalerts.eventalertsintegration.config.EventType;
import org.jetbrains.annotations.Nullable;


public class FamousEvent extends EAObject {
    @Nullable public EventType type;
    @Nullable public String message;
}
