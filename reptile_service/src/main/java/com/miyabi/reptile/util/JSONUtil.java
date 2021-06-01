package com.miyabi.reptile.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * JSON工具
 *
 * @author miyabi
 * @date 2021-04-02-14-09
 * @since 1.0
 **/


public class JSONUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> strToMap(String str) throws JsonProcessingException {
        return (Map<String, String>) objectMapper.readValue(str, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> strToObjectMap(String str) throws JsonProcessingException {
        return (Map<String, Object>) objectMapper.readValue(str, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> strToList(String str) throws JsonProcessingException {
        return (List<Map<String, String>>) objectMapper.readValue(str, List.class);
    }
}
