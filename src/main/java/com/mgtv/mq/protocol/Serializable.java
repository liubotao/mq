package com.mgtv.mq.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

public abstract class Serializable {
    private final static Charset UTF8 = Charset.forName("UTF-8");

    public static byte[] encode(final Object obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T decode(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, UTF8);
        return fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

}
