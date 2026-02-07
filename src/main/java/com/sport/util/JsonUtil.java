package com.sport.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    // Адаптер для LocalDateTime — ВОТ ЭТО ИСПРАВЛЯЕТ ОШИБКУ
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();

    private JsonUtil() { throw new UnsupportedOperationException("Utility class cannot be instantiated"); }

    public static String toJson(Object obj) { return gson.toJson(obj); }
    public static <T> T fromJson(String json, Class<T> clazz) { return gson.fromJson(json, clazz); }
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) { return gson.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType()); }

    // Ответы API
    public static String createSuccessResponse(Object data) { return createResponse(true, "Success", data); }
    public static String createErrorResponse(String errorMessage) { return createResponse(false, errorMessage, null); }

    public static String createResponse(boolean success, String message, Object data) {
        ApiResponse response = new ApiResponse(success, message, data);
        return gson.toJson(response);
    }

    private static class ApiResponse {
        private final boolean success;
        private final String message;
        private final Object data;
        private final long timestamp;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
    }
}