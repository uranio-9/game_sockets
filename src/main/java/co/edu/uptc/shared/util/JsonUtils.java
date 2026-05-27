package co.edu.uptc.shared.util;

import com.google.gson.*;

public class JsonUtils {
    private static final Gson GSON = new Gson();

    /** Serialize any object to a JSON string. */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /** Deserialize a JSON string into the given class. */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * Read only the {@code "type"} field from a raw JSON string
     * without fully deserializing the payload.
     * Returns {@code null} if the field is absent or the JSON is malformed.
     */
    public static String getType(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonElement el = obj.get("type");
            return (el != null && !el.isJsonNull()) ? el.getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
