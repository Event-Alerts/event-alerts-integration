package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonArray;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class EventThreadMessageJsonTest {
    @Test
    void eventThreadMessageRoundTripsToAndFromJson() {
        final JsonObject json = jsonObject(object -> {
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
            object.add("message", jsonObject(message -> {
                message.addProperty("id", "100");
                message.add("content", jsonObject(content -> {
                    content.addProperty("raw", "**Synthetic**");
                    content.addProperty("display", "Synthetic");
                    content.addProperty("stripped", "Synthetic");
                }));
                message.add("attachments", jsonArray(attachments -> {
                    attachments.add(jsonObject(attachment -> {
                        attachment.addProperty("id", "100");
                        attachment.addProperty("name", "attachment-1");
                        attachment.addProperty("url", "https://example.com/attachment-1");
                        attachment.addProperty("proxyUrl", "https://example.com/attachment-1-proxy");
                    }));
                    attachments.add(jsonObject(attachment -> {
                        attachment.addProperty("id", "200");
                        attachment.addProperty("name", "attachment-2");
                    }));
                }));
            }));
        });

        final EventThreadMessage message = GSONProvider.GSON.fromJson(json, EventThreadMessage.class);

        assertNotNull(message.event);
        assertEquals(new ObjectId("cccccccccccccccccccccccc"), message.event.id);
        assertNotNull(message.channel);
        assertEquals(900000000000000103L, message.channel.id);
        assertEquals("general", message.channel.name);
        assertNotNull(message.author);
        assertEquals(900000000000000104L, message.author.id);
        assertEquals("tester-user", message.author.name);
        assertEquals("TESTER-USER", message.author.effectiveName);
        assertNotNull(message.author.player);
        assertNotNull(message.author.player.discord);
        assertEquals(900000000000000105L, message.author.player.discord.id);
        assertNotNull(message.author.player.minecraft);
        assertNotNull(message.author.player.minecraft.uuid);
        assertEquals("dddddddd-eeee-ffff-1111-222222222222", message.author.player.minecraft.uuid.toString());
        assertNotNull(message.message);
        assertEquals(100L, message.message.id);
        assertNotNull(message.message.content);
        assertEquals("**Synthetic**", message.message.content.raw);
        assertEquals("Synthetic", message.message.content.display);
        assertEquals("Synthetic", message.message.content.stripped);
        assertNotNull(message.message.attachments);
        assertEquals(2, message.message.attachments.size());
        assertEquals(100L, message.message.attachments.getFirst().id);
        assertJsonEquals(json, GSONProvider.GSON.toJsonTree(message));
    }

    @Test
    void eventThreadMessageInvalidFieldsBecomeNull() {
        final JsonObject json = jsonObject(object -> {
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
                    player.add("minecraft", jsonObject(minecraft -> minecraft.addProperty("uuid", "not-a-uuid")));
                }));
            }));
            object.add("message", jsonObject(message -> {
                message.addProperty("id", "not-a-number");
                message.add("content", jsonObject(content -> {
                    content.addProperty("raw", 8473);
                    content.addProperty("display", 62464273);
                    content.add("stripped", jsonArray(invalid -> invalid.add("invalid")));
                }));
                message.add("attachments", jsonArray(attachments -> attachments.add(jsonObject(attachment -> {
                    attachment.addProperty("id", "100");
                    attachment.addProperty("name", "attachment-1");
                    attachment.addProperty("url", "https://example.com/attachment-1");
                    attachment.addProperty("proxyUrl", "https://example.com/attachment-1-proxy");
                }))));
            }));
        });

        final EventThreadMessage message = GSONProvider.GSON.fromJson(json, EventThreadMessage.class);

        assertNotNull(message.event);
        assertNotNull(message.channel);
        assertNotNull(message.author);
        assertNotNull(message.author.player);
        assertNotNull(message.author.player.discord);
        assertEquals(900000000000000105L, message.author.player.discord.id);
        assertNotNull(message.author.player.minecraft);
        assertNull(message.author.player.minecraft.uuid);
        assertNotNull(message.message);
        assertNull(message.message.id);
        assertNotNull(message.message.content);
        assertEquals("8473", message.message.content.raw);
        assertEquals("62464273", message.message.content.display);
        assertNull(message.message.content.stripped);
        assertNotNull(message.message.attachments);
        assertEquals(1, message.message.attachments.size());
        assertEquals(100L, message.message.attachments.getFirst().id);

        // Fix purposefully invalid fields on expected object for assertion
        json.getAsJsonObject("author").getAsJsonObject("player").getAsJsonObject("minecraft").remove("uuid");
        final JsonObject messageJson = json.getAsJsonObject("message");
        messageJson.remove("id");
        final JsonObject contentJson = messageJson.getAsJsonObject("content");
        contentJson.remove("raw");
        contentJson.addProperty("raw", "8473");
        contentJson.remove("display");
        contentJson.addProperty("display", "62464273");
        contentJson.remove("stripped");
        assertJsonEquals(json, GSONProvider.GSON.toJsonTree(message));
    }
}
