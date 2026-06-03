package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PlayerJsonTest {
    @Test
    void eaPlayerMatchesSyntheticSubset() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "111111111111111111111111");
            object.add("discord", jsonObject(discord -> {
                discord.addProperty("id", 101010101010101010L);
                discord.addProperty("username", "tester-one");
                discord.add("roles", gg.eventalerts.eventalertsintegration.JsonTestSupport.stringArray("ALPHA", "BETA", "GAMMA"));
            }));
            object.add("minecraft", jsonObject(minecraft -> {
                minecraft.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
                minecraft.addProperty("username", "tester-two");
            }));
            object.add("anniversaries", gg.eventalerts.eventalertsintegration.JsonTestSupport.longArray(2020, 2021, 2022));
        });

        final EAPlayer player = GSONProvider.GSON.fromJson(json, EAPlayer.class);

        assertNotNull(player.discord);
        assertEquals(101010101010101010L, player.discord.id);
        assertNotNull(player.minecraft);
        assertNotNull(player.minecraft.uuid);
        assertEquals("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", player.minecraft.uuid.toString());
        assertNull(player.linkStatus);
        assertJsonEquals(jsonObject(object -> {
            object.add("discord", jsonObject(discord -> discord.addProperty("id", "101010101010101010")));
            object.add("minecraft", jsonObject(minecraft -> minecraft.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
        }), GSONProvider.GSON.toJsonTree(player));
    }

    @Test
    void eaPlayerInvalidNestedFieldsBecomeNull() {
        final JsonObject json = jsonObject(object -> {
            object.add("discord", jsonObject(discord -> discord.addProperty("id", "not-a-number")));
            object.add("minecraft", jsonObject(minecraft -> minecraft.addProperty("uuid", "not-a-uuid")));
        });

        final EAPlayer player = GSONProvider.GSON.fromJson(json, EAPlayer.class);

        assertNotNull(player.discord);
        assertNull(player.discord.id);
        assertNotNull(player.minecraft);
        assertNull(player.minecraft.uuid);
        assertNull(player.linkStatus);
    }
}
