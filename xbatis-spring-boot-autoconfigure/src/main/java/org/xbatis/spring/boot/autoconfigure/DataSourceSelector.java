package org.xbatis.spring.boot.autoconfigure;

import java.util.Map;

public class DataSourceSelector {

    private static final ThreadLocal<String> LOCAL_CLASS = new ThreadLocal<>();
    private static final ThreadLocal<String> LOCAL_METHOD = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> LOCAL_SHARD_VALUE = new ThreadLocal<>();

    public static void setLocalClass(String localClass) {
        LOCAL_CLASS.set(localClass);
    }

    public static String getLocalClass() {
        return LOCAL_CLASS.get();
    }

    public static void setLocalShardValue(Map<String, Object> shardValue) {
        LOCAL_SHARD_VALUE.set(shardValue);
    }

    public static Map<String, Object> getLocalShareValue() {
        return LOCAL_SHARD_VALUE.get();
    }

    public static void setLocalMethod(String localMethod) {
        LOCAL_METHOD.set(localMethod);
    }

    public static String getLocalMethod() {
        return LOCAL_METHOD.get();
    }

    public static void removeAll() {
        LOCAL_CLASS.remove();
        LOCAL_METHOD.remove();
        LOCAL_SHARD_VALUE.remove();
    }
}
