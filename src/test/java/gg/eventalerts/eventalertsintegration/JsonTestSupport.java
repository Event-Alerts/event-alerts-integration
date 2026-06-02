package gg.eventalerts.eventalertsintegration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public final class JsonTestSupport {
    @NotNull
    public static JsonObject jsonObject(@NotNull Consumer<JsonObject> consumer) {
        final JsonObject json = new JsonObject();
        consumer.accept(json);
        return json;
    }

    @NotNull
    public static JsonArray longArray(long @NotNull ... values) {
        final JsonArray array = new JsonArray();
        for (final long value : values) array.add(new JsonPrimitive(value));
        return array;
    }

    @NotNull
    public static JsonArray stringArray(String @NotNull ... values) {
        final JsonArray array = new JsonArray();
        for (final String value : values) array.add(new JsonPrimitive(value));
        return array;
    }

    public static void assertJsonEquals(JsonElement expected, JsonElement actual) {
        Assertions.assertEquals(canonicalize(expected), canonicalize(actual));
    }

    static JsonElement canonicalize(JsonElement element) {
        if (element == null || element.isJsonNull()) return element;
        if (element.isJsonPrimitive()) return element;
        if (element.isJsonObject()) {
            final JsonObject input = element.getAsJsonObject();
            final JsonObject output = new JsonObject();
            input.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                    .forEach(entry -> output.add(entry.getKey(), canonicalize(entry.getValue())));
            return output;
        }

        final JsonArray input = element.getAsJsonArray();
        final List<JsonElement> values = new ArrayList<>();
        for (final JsonElement child : input) values.add(canonicalize(child));
        values.sort(Comparator.comparing(JsonElement::toString));

        final JsonArray output = new JsonArray();
        for (final JsonElement child : values) output.add(child);
        return output;
    }
}
