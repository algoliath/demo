package com.example.demo.aop.postbeanprocessor;


import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Slf4j
public class ControllerAdvice implements MethodInterceptor {

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getMethod().getParameters();
        log.info("arguments={}", arguments);
        invocation.proceed();
//        Model model = (Model) Arrays.stream(arguments).filter(argument -> argument instanceof Model).findAny().orElse(null);
//        Long memberId = (Long) Arrays.stream(arguments).filter(argument -> argument)
        return null;
    }

}
