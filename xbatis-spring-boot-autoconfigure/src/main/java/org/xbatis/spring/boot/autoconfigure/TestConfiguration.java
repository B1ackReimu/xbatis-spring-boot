package org.xbatis.spring.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(A.class)
public class TestConfiguration {

    public TestConfiguration(@Qualifier(value = "b") A a) {
        System.out.println("TestConfiguration:" + a.getClassName());
    }



}

