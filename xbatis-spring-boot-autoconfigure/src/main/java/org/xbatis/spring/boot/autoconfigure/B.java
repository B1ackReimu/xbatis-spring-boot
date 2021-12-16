package org.xbatis.spring.boot.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class B {

    @Bean("b")
    public A a() {
        A a = new A();
        a.setClassName("@Configuration:" + a.getClassName());
        return a;
    }

}
