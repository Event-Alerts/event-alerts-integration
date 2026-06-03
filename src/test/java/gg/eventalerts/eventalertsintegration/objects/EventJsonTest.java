package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.config.PingRole;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.longArray;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.stringArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class EventJsonTest {
    @Test
    void eventFromSyntheticPayloadUsesSupportedFieldsOnly() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "0123456789abcdef01234567");
            object.addProperty("type", "COMMUNITY");
            object.addProperty("channel", "900000000000000001");
            object.addProperty("message", "900000000000000002");
            object.addProperty("controlPanel", "900000000000000003");
            object.addProperty("custom", false);
            object.addProperty("created", "1700000000000");
            object.addProperty("title", "Synthetic Event");
            object.addProperty("host", "900000000000000004");
            object.addProperty("description", "Synthetic description for testing only.");
            object.add("roles", longArray(900000000000000005L));
            object.add("rolesNamed", stringArray("COMMUNITY"));
            object.addProperty("source", "DISCORD");
            object.addProperty("ip", "play.test.example");
            object.add("platforms", stringArray("JAVA"));
            object.addProperty("version", "1.99.0-test");
            object.addProperty("prize", "Synthetic prize");
            object.addProperty("time", "1700003600000");
        });

        final Event event = GSONProvider.GSON.fromJson(json, Event.class);

        assertEquals(new ObjectId("0123456789abcdef01234567"), event.id);
        assertEquals("COMMUNITY", event.type);
        assertEquals(900000000000000001L, event.channel);
        assertEquals(Long.valueOf(900000000000000002L), event.message);
        assertEquals(Long.valueOf(900000000000000003L), event.controlPanel);
        assertNotEquals(Boolean.TRUE, event.custom);
        assertEquals(new Date(1_700_000_000_000L), event.created);
        assertEquals("Synthetic Event", event.title);
        assertEquals(900000000000000004L, event.host);
        assertEquals("Synthetic description for testing only.", event.description);
        assertEquals(Set.of(900000000000000005L), event.roles);
        assertEquals(Set.of(PingRole.COMMUNITY), event.rolesNamed);
        assertNull(event.server);
        assertNull(event.media);
        assertEquals("play.test.example", event.ip);
        assertEquals(Set.of(Event.Platform.JAVA), event.platforms);
        assertEquals("1.99.0-test", event.version);
        assertEquals("Synthetic prize", event.prize);
        assertNull(event.maxPlayers);
        assertEquals(new Date(1_700_003_600_000L), event.time);
        assertNull(event.subscribers);

        assertJsonEquals(jsonObject(object -> {
            object.addProperty("id", "0123456789abcdef01234567");
            object.addProperty("type", "COMMUNITY");
            object.addProperty("channel", "900000000000000001");
            object.addProperty("message", "900000000000000002");
            object.addProperty("controlPanel", "900000000000000003");
            object.addProperty("custom", false);
            object.addProperty("created", "1700000000000");
            object.addProperty("title", "Synthetic Event");
            object.addProperty("host", "900000000000000004");
            object.addProperty("description", "Synthetic description for testing only.");
            object.add("roles", stringArray("900000000000000005"));
            object.add("rolesNamed", stringArray("COMMUNITY"));
            object.addProperty("ip", "play.test.example");
            object.add("platforms", stringArray("JAVA"));
            object.addProperty("version", "1.99.0-test");
            object.addProperty("prize", "Synthetic prize");
            object.addProperty("time", "1700003600000");
        }), GSONProvider.GSON.toJsonTree(event));
    }

    @Test
    void unsupportedPlatformValueLeavesPlatformsNull() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "0123456789abcdef01234567");
            object.addProperty("type", "COMMUNITY");
            object.addProperty("channel", "900000000000000001");
            object.addProperty("custom", false);
            object.addProperty("created", "1700000000000");
            object.addProperty("host", "900000000000000004");
            object.add("platforms", stringArray("JAVA", "NOT_A_REAL_PLATFORM"));
        });

        final Event event = GSONProvider.GSON.fromJson(json, Event.class);

        assertEquals(Set.of(Event.Platform.JAVA), event.platforms);
    }
}
