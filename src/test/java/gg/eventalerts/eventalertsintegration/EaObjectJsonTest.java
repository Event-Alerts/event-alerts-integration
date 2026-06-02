package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.objects.EAObject;
import org.junit.jupiter.api.Test;

import static gg.eventalerts.eventalertsintegration.JsonTestSupport.assertJsonEquals;
import static gg.eventalerts.eventalertsintegration.JsonTestSupport.jsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EaObjectJsonTest {
    @Test
    void toJsonRecursesIntoNestedEaObjectsAndSkipsNulls() {
        final EaObjectSampleObject object = new EaObjectSampleObject(
                "root",
                null,
                new EaObjectSampleObject.Nested("child", 7));

        assertJsonEquals(
                jsonObject(json -> {
                    json.addProperty("name", "root");
                    json.add("nested", jsonObject(nested -> {
                        nested.addProperty("label", "child");
                        nested.addProperty("count", 7);
                    }));
                }),
                object.toJson());
    }

    @Test
    void toStringUsesJsonRepresentation() {
        final EaObjectSampleObject object = new EaObjectSampleObject("root", "optional", new EaObjectSampleObject.Nested("child", 7));

        assertEquals(object.toJson().toString(), object.toString());
    }
}
