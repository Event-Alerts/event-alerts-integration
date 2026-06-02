package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.config.EventType;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FamousEventJsonTest {

    @Test
    void famousEventRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("type", "FAMOUS");
            object.addProperty("message", "You won!");
        });

        final FamousEvent famousEvent = new FamousEvent(json);

        assertEquals(EventType.FAMOUS, famousEvent.type);
        assertEquals("You won!", famousEvent.message);
        assertJsonEquals(json, famousEvent.toJson());
    }
}
