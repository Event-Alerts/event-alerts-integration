package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.config.EventType;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class FamousEventJsonTest {

    @Test
    void famousEventRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("type", "FAMOUS");
            object.addProperty("message", "You won!");
        });

        final FamousEvent famousEvent = GSONProvider.GSON.fromJson(json, FamousEvent.class);

        assertEquals(EventType.FAMOUS, famousEvent.type);
        assertEquals("You won!", famousEvent.message);
        assertJsonEquals(json, GSONProvider.GSON.toJsonTree(famousEvent));
    }

    @Test
    void famousEventMissingMessageLeavesFieldNull() {
        final JsonObject json = jsonObject(object -> object.addProperty("type", "FAMOUS"));

        final FamousEvent famousEvent = GSONProvider.GSON.fromJson(json, FamousEvent.class);

        assertEquals(EventType.FAMOUS, famousEvent.type);
        assertNull(famousEvent.message);
        assertJsonEquals(json, GSONProvider.GSON.toJsonTree(famousEvent));
    }
}
