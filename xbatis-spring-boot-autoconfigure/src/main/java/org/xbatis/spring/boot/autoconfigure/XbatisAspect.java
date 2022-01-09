package org.xbatis.spring.boot.autoconfigure;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Aspect
@Component
public class XbatisAspect {

    @Pointcut("execution(public * org.xbatis.spring.boot.autoconfigure.test.mapper.*.*(..)) ")
    public void sss() {

    }

    @Around("sss()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        System.out.println("parameterNames:" + parameterNames);
        String declaringTypeName = signature.getDeclaringTypeName().intern();
        DataSourceSelector.setLocalClass(declaringTypeName);
        String statementId = declaringTypeName + "." + signature.getName().intern();
        DataSourceSelector.setLocalMethod(statementId);
        DataSourceSelector.setLocalShardValue(new HashMap<>());
        XbatisInterceptor.STATEMENT_ID.set(statementId);
        return joinPoint.proceed();
    }

}
