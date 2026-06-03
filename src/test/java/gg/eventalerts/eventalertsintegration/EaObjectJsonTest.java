package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.json.GSONProvider;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.UUID;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.stringArray;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EaObjectJsonTest {
    @Test
    void toJsonRecursesIntoNestedEaObjectsAndSkipsNulls() {
        final EaObjectSampleObject object = new EaObjectSampleObject(
                "root",
                null,
                new EaObjectSampleObject.Nested(
                        "child",
                        new Date(1_700_000_000_123L),
                        12.5,
                        13.5,
                        14,
                        15,
                        16L,
                        17L,
                        new ObjectId("507f1f77bcf86cd799439011"),
                        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                        CrossBan.Status.REMOVED,
                        new LinkedHashSet<>(java.util.List.of("alpha", "beta")),
                        new LinkedHashSet<>(java.util.List.of(CrossBan.Status.ADDED, CrossBan.Status.EDITED)),
                        new EaObjectSampleObject.Leaf("leaf", null)));

        assertJsonEquals(
                jsonObject(json -> {
                    json.addProperty("name", "root");
                    json.add("nested", jsonObject(nested -> {
                        nested.addProperty("label", "child");
                        nested.addProperty("date", "1700000000123");
                        nested.addProperty("boxedDouble", 12.5);
                        nested.addProperty("primitiveDouble", 13.5);
                        nested.addProperty("boxedInteger", 14);
                        nested.addProperty("primitiveInteger", 15);
                        nested.addProperty("boxedLong", "16");
                        nested.addProperty("primitiveLong", "17");
                        nested.addProperty("objectId", "507f1f77bcf86cd799439011");
                        nested.addProperty("uuid", "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
                        nested.addProperty("status", "REMOVED");
                        nested.add("stringSet", stringArray("alpha", "beta"));
                        nested.add("statusSet", stringArray("ADDED", "EDITED"));
                        nested.add("leaf", jsonObject(leaf -> leaf.addProperty("name", "leaf")));
                    }));
                }),
                GSONProvider.GSON.toJsonTree(object));
    }

    @Test
    void toStringUsesJsonRepresentation() {
        final EaObjectSampleObject object = new EaObjectSampleObject(
                "root",
                "optional",
                new EaObjectSampleObject.Nested(
                        "child",
                        new Date(1_700_000_000_123L),
                        12.5,
                        13.5,
                        14,
                        15,
                        16L,
                        17L,
                        new ObjectId("507f1f77bcf86cd799439011"),
                        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                        CrossBan.Status.REMOVED,
                        new LinkedHashSet<>(java.util.List.of("alpha", "beta")),
                        new LinkedHashSet<>(java.util.List.of(CrossBan.Status.ADDED, CrossBan.Status.EDITED)),
                        new EaObjectSampleObject.Leaf("leaf", null)));

        assertEquals(GSONProvider.GSON.toJson(object), object.toString());
    }
}
