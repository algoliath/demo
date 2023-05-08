package com.example.demo.aop.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LogAdvice {

    @Around("com.example.demo.aop.pointcut.Pointcuts.logger()")
    public Object logAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        log.info("signature={}", proceedingJoinPoint.getSignature());
        log.info("args={}", args);
        return proceedingJoinPoint.proceed();
    }


}
