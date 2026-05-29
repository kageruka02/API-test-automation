package com.kageruka.apitest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

public class JsonLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> loadAsMap(String resourcePath) {
        try (InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON resource: " + resourcePath, e);
        }
    }

    public static <T> T loadAs(String resourcePath, TypeReference<T> typeReference) {
        try (InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(is, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON resource: " + resourcePath, e);
        }
    }
}
