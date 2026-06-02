package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlayerConnectionJsonTest {
    @Test
    void playerConnectionRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
            object.addProperty("username", "tester-connection");
            object.addProperty("timestamp", "1700000000123");
            object.addProperty("type", "JOIN");
        });

        final PlayerConnection connection = new PlayerConnection(json);

        assertEquals(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"), connection.uuid);
        assertEquals("tester-connection", connection.username);
        assertEquals(new Date(1_700_000_000_123L), connection.timestamp);
        assertEquals(PlayerConnection.Type.JOIN, connection.type);
        assertJsonEquals(json, connection.toJson());
    }
}
