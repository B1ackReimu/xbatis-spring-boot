package org.xbatis.spring.boot.autoconfigure;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class XbatisAspect {

    @Pointcut("execution(public * org.xbatis.spring.boot.autoconfigure.test.mapper.*.*(..)) ")
    public void sss(){

    }

    @Around("sss()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("sss");
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        DataSourceSelector.setLocalClass(declaringTypeName);
        DataSourceSelector.setLocalMethod(declaringTypeName+"."+joinPoint.getSignature().getName());
        DataSourceSelector.setLocalShardValue("");
        return joinPoint.proceed();
    }

}
