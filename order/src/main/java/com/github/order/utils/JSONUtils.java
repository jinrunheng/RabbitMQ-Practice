package com.github.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @Author Dooby Kim
 * @Date 2022/10/30 4:49 下午
 * @Version 1.0
 * 基于 Jackson 实现的 JSON 字符串与对象之间的序列化与反序列化工具类
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

    public static Object jsonToObject(byte[] jsonStr, Class<?> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
