package com.example.demo.aop.advice;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class PageAdvice {
    @Around("com.example.demo.aop.pointcut.Pointcuts.pager()")
    public Object logAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        PageHelper.startPage(1, 5);
        log.info("page info={}");
        return proceedingJoinPoint.proceed();
    }
}
