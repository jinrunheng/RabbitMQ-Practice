package com.github.restaurant.utils;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 6:15 下午
 * @Version 1.0
 */
public class JSONUtils {

    /**
     * 将 Java 对象序列化为 JSON 字符串
     *
     * @return
     */
    public static String objectToJson(Object obj) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将 JSON 字符串反序列化为 Java 对象
     *
     * @param jsonStr
     * @return
     */
    public static Object jsonToObject(String jsonStr, Class<?> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

