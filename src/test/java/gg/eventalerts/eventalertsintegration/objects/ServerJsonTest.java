package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.longArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ServerJsonTest {
    @Test
    void serverWithNameMatchesSyntheticSubset() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "333333333333333333333333");
            object.addProperty("serverId", "4444444444444444444");
            object.addProperty("created", "1720000000000");
            object.add("representatives", longArray(555555555555555555L));
            object.addProperty("name", "Test Server");
            object.addProperty("description", "test.example");
            object.addProperty("invite", "TESTCODE");
            object.addProperty("color", 10494192);
            object.addProperty("thumbnail", "http://test.example/thumbnail.png");
            object.add("gets", jsonObject(gets -> gets.addProperty("555555555555555555", 1)));
        });

        final Server server = new Server(json);

        assertEquals("Test Server", server.name);
        assertJsonEquals(jsonObject(object -> object.addProperty("name", "Test Server")), server.toJson());
    }

    @Test
    void serverWithoutNameLeavesFieldNull() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "666666666666666666666666");
            object.addProperty("created", "1710000000000");
            object.add("representatives", longArray(777777777777777777L));
            object.add("gets", jsonObject(gets -> gets.addProperty("777777777777777777", 1)));
        });

        final Server server = new Server(json);

        assertNull(server.name);
        assertJsonEquals(new JsonObject(), server.toJson());
    }
}
