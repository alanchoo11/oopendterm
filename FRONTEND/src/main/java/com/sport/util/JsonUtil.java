package com.sport.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON serialization/deserialization.
 * Uses Gson library for simplicity.
 */
public class JsonUtil {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private JsonUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts an object to JSON string.
     *
     * @param obj the object to convert
     * @return JSON string
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Converts JSON string to object of specified class.
     *
     * @param json the JSON string
     * @param clazz the target class
     * @param <T> the type
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Converts JSON string to List of objects.
     *
     * @param json the JSON string
     * @param clazz the element class
     * @param <T> the element type
     * @return the list of deserialized objects
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        return gson.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType());
    }

    /**
     * Converts JSON string to Map.
     *
     * @param json the JSON string
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> fromJsonToMap(String json) {
        return gson.fromJson(json, Map.class);
    }

    /**
     * Creates a standard API response.
     *
     * @param success whether the operation was successful
     * @param message response message
     * @param data response data
     * @return JSON response string
     */
    public static String createResponse(boolean success, String message, Object data) {
        ApiResponse response = new ApiResponse(success, message, data);
        return toJson(response);
    }

    /**
     * Creates an error response.
     *
     * @param errorMessage the error message
     * @return JSON error response
     */
    public static String createErrorResponse(String errorMessage) {
        return createResponse(false, errorMessage, null);
    }

    /**
     * Creates a success response.
     *
     * @param data the response data
     * @return JSON success response
     */
    public static String createSuccessResponse(Object data) {
        return createResponse(true, "Success", data);
    }

    /**
     * Inner class for standard API response structure.
     */
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
