package com.example.demo.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.example.demo.web.controller..search*(.., com.example.demo.domain.data.vo.template.entity.EntitySearchForm, ..))")
    public void pager(){

    }

    @Pointcut("execution(* com.example.demo.web.controller..*(..))")
    public void logger(){

    }

    @Pointcut("execution(* com.example.demo.web.controller.EntityController.*(..,org.springframework.ui.Model, ..))")
    public void entityModelBuilder() {

    }

    @Pointcut("execution(* com.example.demo.web.controller.QueryController.*(..,org.springframework.ui.Model, ..))")
    public void queryModelBuilder() {
    }


    @Pointcut("execution(* com.example.demo.web.controller.QueryController.add*(..)) || execution(* com.example.demo.web.controller.QueryController.update*(..)) || execution(* com.example.demo.web.controller.QueryController.remove*(..))")
    public void queryConverter() {
    }

}
