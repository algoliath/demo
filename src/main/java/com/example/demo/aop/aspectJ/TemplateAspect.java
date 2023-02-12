package com.example.demo.aop.aspectJ;

import com.example.demo.domain.database.form.SQLForm;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
import com.example.demo.domain.template.form.QueryTemplateForm;
import com.example.demo.util.controller.ControllerUtils;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.util.validation.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
public class TemplateAspect {

    private final TemplateFormProvider templateFormProvider;
    private Annotation annotation;

    @Autowired
    public TemplateAspect(TemplateFormProvider templateFormProvider) {
        this.templateFormProvider = templateFormProvider;
    }

    @Pointcut("execution(* com.example.demo.web.template.QueryController.add*(..))," +
              "execution(* com.example.demo.web.template.QueryController.remove*(..))" +
              "execution(* com.example.demo.web.template.QueryController.edit*(..))")
    private void queryController(){

    }

    @Around("queryController()")
    public Object queryBuildAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        log.info("[signature]:{}", proceedingJoinPoint.getSignature());
        log.info("[args]:{}", proceedingJoinPoint.getArgs());
        log.info("[parameters]:{}", signature.getParameterNames());
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
//
//        String memberId = null;
//        for (Annotation[] parameterAnnotation : parameterAnnotations) {
//            for (Annotation annotation : parameterAnnotation) {
//                if(annotation instanceof PathVariable){;
//                    PathVariable targetAnnotation = (PathVariable) annotation;
//                    memberId = targetAnnotation
//                }
//            }
//        }
//
//        log.info("memberId={}", memberId);
//        if(memberId != null){
//            QueryTemplateForm templateForm = templateFormProvider.getQueryTemplateForm(memberId);
//            log.info("[queryTemplateForm]:{}", templateForm);
//            SQLForm sqlForm = templateForm.getSQLForm();
//            for(SQLBlock sqlBlock: sqlForm.getSqlBlockList()){
//                sqlBlock.setSqlQuery(QueryUtils.convertSQLBlock(sqlBlock));
//            }
//            sqlForm.setSqlQuery(QueryUtils.convertSQLBlocks(sqlForm.getSqlBlockList()));
//            templateFormProvider.setQueryTemplateForm(memberId, templateForm);
//        }

        Object result = proceedingJoinPoint.proceed();
        log.info("[result]:{}", result);
        return result;
    }


}
