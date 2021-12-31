package org.xbatis.spring.boot.autoconfigure;

import org.xbatis.spring.boot.config.XbatisDataSource;

public class Test {

    private static final ThreadLocal<String> dataKey = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(() -> {
            dataKey.set("haha");
            System.out.println(Thread.currentThread().getId() + ":" + dataKey.get());
        }).start();
        new Thread(() -> {
            dataKey.set("hehe");
            System.out.println(Thread.currentThread().getId() + ":" + dataKey.get());
        }).start();
        System.out.println(dataKey.get());
        XbatisDataSource xbatisDataSource = new XbatisDataSource();
    }

}
