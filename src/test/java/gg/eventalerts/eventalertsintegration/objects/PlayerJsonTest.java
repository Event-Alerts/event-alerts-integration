package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


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

        final EAPlayer player = new EAPlayer(json);

        assertEquals(new EAPlayer.Discord(101010101010101010L), player.discord);
        assertEquals(new EAPlayer.Minecraft(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")), player.minecraft);
        assertNull(player.linkStatus);
        assertJsonEquals(jsonObject(object -> {
            object.add("discord", jsonObject(discord -> discord.addProperty("id", "101010101010101010")));
            object.add("minecraft", jsonObject(minecraft -> minecraft.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
        }), player.toJson());
    }
}
