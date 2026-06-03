package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class JsonAdapterTest {
    // String
    @Test
    void stringAdapterWritesStringsAndNullsOutInvalidValues() {
        assertEquals("\"tester\"", GSONProvider.GSON.toJson("tester", String.class));
        assertEquals("tester", GSONProvider.GSON.fromJson("\"tester\"", String.class));
        assertNull(GSONProvider.GSON.fromJson("{}", String.class));
    }

    // Date
    @Test
    void dateAdapterWritesMillisAsJsonString() {
        assertEquals("\"1700000000123\"", GSONProvider.GSON.toJson(new Date(1_700_000_000_123L), Date.class));
    }
    @Test
    void dateAdapterReadsMillisFromJsonString() {
        assertEquals(new Date(1_700_000_000_123L), GSONProvider.GSON.fromJson("\"1700000000123\"", Date.class));
    }
    @Test
    void gsonRoundTripsDateUsingCustomAdapter() {
        final Date original = new Date(1_700_000_000_123L);

        assertEquals(original, GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(original), Date.class));
    }

    // Long
    @Test
    void longAdapterWritesLongsAsJsonStrings() {
        assertEquals("\"1700000000123\"", GSONProvider.GSON.toJson(1_700_000_000_123L, Long.class));
        assertEquals(Long.valueOf(1_700_000_000_123L), GSONProvider.GSON.fromJson("\"1700000000123\"", Long.class));
        assertNull(GSONProvider.GSON.fromJson("{}", Long.class));
    }

    // ObjectId
    @Test
    void objectIdAdapterWritesObjectIdAsJsonString() {
        assertEquals("\"507f1f77bcf86cd799439011\"", GSONProvider.GSON.toJson(new ObjectId("507f1f77bcf86cd799439011"), ObjectId.class));
    }
    @Test
    void objectIdAdapterReadsObjectIdFromJsonString() {
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), GSONProvider.GSON.fromJson("\"507f1f77bcf86cd799439011\"", ObjectId.class));
    }
    @Test
    void gsonRoundTripsObjectIdUsingCustomAdapter() {
        final ObjectId original = new ObjectId("507f1f77bcf86cd799439011");

        assertEquals(original, GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(original), ObjectId.class));
    }

    // UUID
    @Test
    void uuidAdapterWritesAndReadsStrings() {
        final UUID original = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        assertEquals("\"aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\"", GSONProvider.GSON.toJson(original, UUID.class));
        assertEquals(original, GSONProvider.GSON.fromJson("\"aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\"", UUID.class));
        assertNull(GSONProvider.GSON.fromJson("{}", UUID.class));
    }

    // Enum
    @Test
    void enumAdapterWritesNamesAndNullsOutInvalidValues() {
        assertEquals("\"REMOVED\"", GSONProvider.GSON.toJson(CrossBan.Status.REMOVED, CrossBan.Status.class));
        assertEquals(CrossBan.Status.REMOVED, GSONProvider.GSON.fromJson("\"REMOVED\"", CrossBan.Status.class));
        assertNull(GSONProvider.GSON.fromJson("\"NOT_A_REAL_STATUS\"", CrossBan.Status.class));
    }
}
