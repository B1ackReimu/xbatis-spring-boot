package org.xbatis.spring.boot.autoconfigure;

public class DataSourceSelector {

    private static final ThreadLocal<String> LOCAL_CLASS = new ThreadLocal<>();
    private static final ThreadLocal<String> LOCAL_METHOD = new ThreadLocal<>();
    private static final ThreadLocal<String[]> LOCAL_SHARD_VALUE = new ThreadLocal<>();

    public static void setLocalClass(String localClass){
        LOCAL_CLASS.set(localClass);
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

    public static void setLocalMethod(String localMethod){
        LOCAL_METHOD.set(localMethod);
    }

    protected static String getLocalMethod(){
        return LOCAL_METHOD.get();
    }

}
