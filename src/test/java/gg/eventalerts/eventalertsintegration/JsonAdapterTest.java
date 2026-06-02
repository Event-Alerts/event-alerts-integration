package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JsonAdapterTest {
    // Date
    @Test
    void dateAdapterWritesMillisAsJsonString() throws Exception {
        assertEquals("\"1700000000123\"", GSONProvider.GSON.toJson(new Date(1_700_000_000_123L), Date.class));
    }
    @Test
    void dateAdapterReadsMillisFromJsonString() throws Exception {
        assertEquals(new Date(1_700_000_000_123L), GSONProvider.GSON.fromJson("\"1700000000123\"", Date.class));
    }
    @Test
    void gsonRoundTripsDateUsingCustomAdapter() {
        final Date original = new Date(1_700_000_000_123L);

        assertEquals(original, GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(original), Date.class));
    }

    // ObjectId
    @Test
    void objectIdAdapterWritesObjectIdAsJsonString() throws Exception {
        assertEquals("\"507f1f77bcf86cd799439011\"", GSONProvider.GSON.toJson(new ObjectId("507f1f77bcf86cd799439011"), ObjectId.class));
    }
    @Test
    void objectIdAdapterReadsObjectIdFromJsonString() throws Exception {
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), GSONProvider.GSON.fromJson("\"507f1f77bcf86cd799439011\"", ObjectId.class));
    }
    @Test
    void gsonRoundTripsObjectIdUsingCustomAdapter() {
        final ObjectId original = new ObjectId("507f1f77bcf86cd799439011");

        assertEquals(original, GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(original), ObjectId.class));
    }
}
