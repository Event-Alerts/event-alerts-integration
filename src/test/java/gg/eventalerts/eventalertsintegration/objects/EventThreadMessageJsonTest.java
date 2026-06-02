package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class EventThreadMessageJsonTest {
    @Test
    void eventThreadMessageRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("messageId", "msg-100");
            object.add("event", jsonObject(event -> {
                event.addProperty("id", "cccccccccccccccccccccccc");
                event.addProperty("type", "event");
                event.addProperty("channel", "900000000000000101");
                event.addProperty("custom", false);
                event.addProperty("created", "1700000000000");
                event.addProperty("host", "900000000000000102");
            }));
            object.add("channel", jsonObject(channel -> {
                channel.addProperty("id", "900000000000000103");
                channel.addProperty("name", "general");
            }));
            object.add("author", jsonObject(author -> {
                author.addProperty("id", "900000000000000104");
                author.addProperty("name", "tester-user");
                author.addProperty("effectiveName", "TESTER-USER");
                author.add("player", jsonObject(player -> {
                    player.add("discord", jsonObject(discord -> discord.addProperty("id", "900000000000000105")));
                    player.add("minecraft", jsonObject(minecraft -> minecraft.addProperty("uuid", "dddddddd-eeee-ffff-1111-222222222222")));
                }));
            }));
            object.add("content", jsonObject(content -> {
                content.addProperty("raw", "**Synthetic**");
                content.addProperty("display", "Synthetic");
                content.addProperty("stripped", "Synthetic");
            }));
        });

        final EventThreadMessage message = new EventThreadMessage(json);

        assertEquals("msg-100", message.messageId);
        assertEquals(new ObjectId("cccccccccccccccccccccccc"), message.event.id);
        assertEquals(900000000000000103L, message.channel.id);
        assertEquals("general", message.channel.name);
        assertEquals(900000000000000104L, message.author.id);
        assertEquals("tester-user", message.author.name);
        assertEquals("TESTER-USER", message.author.effectiveName);
        assertNotNull(message.author.player);
        assertEquals("**Synthetic**", message.content.raw);
        assertJsonEquals(json, message.toJson());
    }
}
