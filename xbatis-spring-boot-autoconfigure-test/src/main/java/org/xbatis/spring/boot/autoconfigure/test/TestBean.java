package org.xbatis.spring.boot.autoconfigure.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbatis.spring.boot.autoconfigure.A;

@Configuration
public class TestBean {

    @Bean
    public A a() {
        A a = new A();
        a.setClassName(A.class.getName());
        System.out.println("@Bean:" + a.getClassName());
        return a;
    }

    @Bean
    public A b() {
        A a = new A();
        a.setClassName("@Configuration:" + a.getClass().getName());
        return a;
    }
}
