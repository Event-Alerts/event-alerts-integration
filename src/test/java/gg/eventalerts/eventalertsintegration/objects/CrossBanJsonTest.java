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


public class CrossBanJsonTest {
    @Test
    void crossBanMatchesSyntheticSubset() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("id", "222222222222222222222222");
            object.addProperty("discordId", "333333333333333333");
            object.addProperty("minecraftUuid", "bbbbbbbb-cccc-dddd-eeee-ffffffffffff");
            object.addProperty("reason", "Synthetic moderation reason");
            object.addProperty("created", "1750000000000");
        });

        final CrossBan crossBan = GSONProvider.GSON.fromJson(json, CrossBan.class);

        assertEquals(UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"), crossBan.minecraftUuid);
        assertEquals("Synthetic moderation reason", crossBan.reason);
        assertNull(crossBan.expiration);
        assertNull(crossBan.status);
        assertJsonEquals(jsonObject(object -> {
            object.addProperty("minecraftUuid", "bbbbbbbb-cccc-dddd-eeee-ffffffffffff");
            object.addProperty("reason", "Synthetic moderation reason");
        }), GSONProvider.GSON.toJsonTree(crossBan));
    }

    @Test
    void crossBanInvalidUuidLeavesOtherFieldsIntact() {
        final JsonObject json = jsonObject(object -> {
            object.addProperty("minecraftUuid", "not-a-uuid");
            object.addProperty("reason", "Still valid");
            object.addProperty("expiration", "1700000000123");
        });

        final CrossBan crossBan = GSONProvider.GSON.fromJson(json, CrossBan.class);

        assertNull(crossBan.minecraftUuid);
        assertEquals("Still valid", crossBan.reason);
        assertEquals(new Date(1_700_000_000_123L), crossBan.expiration);
    }
}
