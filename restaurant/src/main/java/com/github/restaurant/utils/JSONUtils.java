package com.github.restaurant.utils;

import com.alibaba.fastjson2.JSON;

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
        return JSON.toJSONString(obj);
    }

    /**
     * 将 JSON 字符串反序列化为 Java 对象
     *
     * @param jsonStr
     * @return
     */
    public static Object jsonToObject(String jsonStr, Class<?> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }
}

