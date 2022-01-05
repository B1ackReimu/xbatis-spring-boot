package org.xbatis.spring.boot.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NameSpace {

    String value() default "";

}
