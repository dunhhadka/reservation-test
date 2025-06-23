package org.example.reportetl.external.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    private static final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        return mapper;
    }

    public static <T> T unmarshal(String value, Class<T> clazz) {
        return mapper.convertValue(value, clazz);
    }
}
