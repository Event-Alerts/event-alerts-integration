package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class PlayerConnectionJsonTest {
    @Test
    void playerConnectionRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
            object.addProperty("username", "tester-connection");
            object.addProperty("timestamp", "1700000000123");
            object.addProperty("type", "JOIN");
        });

        final PlayerConnection connection = GSONProvider.GSON.fromJson(json, PlayerConnection.class);

        assertEquals(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"), connection.uuid);
        assertEquals("tester-connection", connection.username);
        assertEquals(new Date(1_700_000_000_123L), connection.timestamp);
        assertEquals(PlayerConnection.Type.JOIN, connection.type);
        assertJsonEquals(json, GSONProvider.GSON.toJsonTree(connection));
    }

    @Test
    void playerConnectionInvalidFieldsBecomeNull() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("uuid", "not-a-uuid");
            object.addProperty("username", "tester-connection");
            object.addProperty("timestamp", "not-a-date");
            object.addProperty("type", "NOT_A_REAL_TYPE");
        });

        final PlayerConnection connection = GSONProvider.GSON.fromJson(json, PlayerConnection.class);

        assertNull(connection.uuid);
        assertEquals("tester-connection", connection.username);
        assertNull(connection.timestamp);
        assertNull(connection.type);
    }
}
