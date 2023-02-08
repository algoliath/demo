package com.example.demo.aop.aspectJ;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class AspectV1 {

    @Pointcut("execution(* com.example.demo.domain.database.mapper..*(..))")
    private void sqlLogger(){

    }


    @Around("sqlLogger()")
    public Object aroundMV(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("[signature]:{}", proceedingJoinPoint.getSignature());
        log.info("[args]:{}", proceedingJoinPoint.getArgs());
        Object result = proceedingJoinPoint.proceed();
        log.info("[result]:{}", result);
        return result;
    }


}
