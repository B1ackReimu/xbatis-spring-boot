package org.xbatis.spring.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@AutoConfigureAfter(value = TestConfiguration.class)
@Configuration
public class TestConfiguration2 {

    private A a;

    public TestConfiguration2(@Qualifier(value = "b") A a) {
        this.a = a;
        System.out.println("TestConfiguration2:" + a.getClassName());
    }

}
