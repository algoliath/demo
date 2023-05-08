package com.example.demo.aop.postbeanprocessor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;

public class AutoProxyConfig {

    @Bean
    public Advisor proxyAdvisor(){
        Advice advice = new ControllerAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* demo.web.template..*(..))");
        Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
        return advisor;
    }

}
