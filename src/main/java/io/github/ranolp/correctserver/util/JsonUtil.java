package io.github.ranolp.correctserver.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.function.Function;
import java.util.function.Predicate;

public class JsonUtil {
    private JsonUtil() {
        throw new UnsupportedOperationException("You cannot instantiate JsonUtil");
    }
    public static final JsonPrimitive TRUE = new JsonPrimitive(true);

    public static <T> T getOrDefault(JsonObject object, String key, Predicate<JsonPrimitive> filter,
                                      Function<JsonPrimitive, T> converter, T defaultValue) {
        if (object.has(key)) {
            JsonElement element = object.get(key);
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (filter.test(primitive)) {
                    return converter.apply(primitive);
                }
            }
        }
        return defaultValue;
    }

    public static String getString(JsonObject object, String key) {
        return getOrDefault(object, key, JsonPrimitive::isString, JsonPrimitive::getAsString, null);
    }
    public static int getInt(JsonObject object, String key) {
        return getOrDefault(object, key, JsonPrimitive::isNumber, JsonPrimitive::getAsInt, 0);
    }

    public static boolean getBoolean(JsonObject object, String key) {
        return getOrDefault(object, key, JsonPrimitive::isBoolean, JsonPrimitive::getAsBoolean, false);
    }
}
