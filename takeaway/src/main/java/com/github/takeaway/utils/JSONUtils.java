package com.github.takeaway.utils;

import com.alibaba.fastjson2.JSON;

/**
 * @Author Dooby Kim
 * @Date 2022/10/30 4:49 下午
 * @Version 1.0
 * 基于 fastjson2 的，实现 JSON 字符串与对象之间的序列化与反序列化工具类
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
    public static Object jsonToObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }
}
