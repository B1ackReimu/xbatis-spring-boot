package org.xbatis.spring.boot.autoconfigure;

public class DataSourceSelector {

    private static final ThreadLocal<String> LOCAL_CLASS = new ThreadLocal<>();
    private static final ThreadLocal<String[]> LOCAL_SHARD_VALUE = new ThreadLocal<>();

    public static void setLocalClass(String method){
        LOCAL_CLASS.set(method);
    }

    protected static String getLocalClass(){
        return LOCAL_CLASS.get();
    }

    public static void setLocalShardValue(String... shardValue){
        LOCAL_SHARD_VALUE.set(shardValue);
    }

    protected static String[] getLocalShareValue(){
        return LOCAL_SHARD_VALUE.get();
    }

}
