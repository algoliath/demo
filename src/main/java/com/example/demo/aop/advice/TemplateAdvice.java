package com.example.demo.aop.advice;

import com.example.demo.domain.data.vo.template.query.QueryBuilderForm;
import com.example.demo.domain.data.vo.template.query.QueryTemplateForm;
import com.example.demo.util.database.sql.QueryBuilderUtils;
import com.example.demo.util.template.TemplateFormProvider;
import org.aspectj.lang.annotation.Aspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import static com.example.demo.util.model.ModelUtils.*;
import static com.example.demo.util.database.sql.QueryBuilderUtils.*;


@Slf4j
@Aspect
public class TemplateAdvice {

    @Autowired
    private HttpServletRequest request;
    private final TemplateFormProvider templateFormProvider;

    @Autowired
    public TemplateAdvice(TemplateFormProvider templateFormProvider) {
        this.templateFormProvider = templateFormProvider;
    }


    @Around(value = "com.example.demo.aop.pointcut.Pointcuts.entityModelBuilder()")
    public Object entityModelBuilderAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        String memberId = extractMemberIdFromUri(request.getRequestURI());

        Model model = null;
        for (Object arg : args) {
            if (arg instanceof Model) {
                model = (Model) arg;
            }
        }
        if(model == null){
            return proceedingJoinPoint.proceed();
        }
        sendForwardModelAttributes(model, templateFormProvider.getEntityTemplateForm(memberId));
        Object proceed = proceedingJoinPoint.proceed();
        sendPostModelAttributes(model, templateFormProvider.getEntityTemplateForm(memberId));
        return proceed;
    }

    @Around(value = "com.example.demo.aop.pointcut.Pointcuts.queryModelBuilder()")
    public Object queryModelBuilderAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String memberId = extractMemberIdFromUri(request.getRequestURI());
        Model model = null;
        for (Object arg : proceedingJoinPoint.getArgs()) {
            if (arg instanceof Model) {
                model = (Model) arg;
            }
        }
        if(model == null){
            return proceedingJoinPoint.proceed();
        }
        sendForwardModelAttributes(model, templateFormProvider.getQueryTemplateForm(memberId));
        Object proceed = proceedingJoinPoint.proceed();
        sendPostModelAttributes(model, templateFormProvider.getQueryTemplateForm(memberId));
        return proceed;
    }

    @Around("com.example.demo.aop.pointcut.Pointcuts.queryConverter()")
    public Object queryConverterAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        String memberId = extractMemberIdFromUri(request.getRequestURI());

        if (memberId != null) {
            QueryTemplateForm templateForm = templateFormProvider.getQueryTemplateForm(memberId);
            QueryBuilderForm queryBuilderForm = templateForm.getQueryBuilderForm();
            queryBuilderForm.getQueryBlockList().stream().iterator().forEachRemaining(sqlBlock -> sqlBlock.setSqlQuery(QueryBuilderUtils.convertSQLBlock(sqlBlock)));
            queryBuilderForm.setSqlQuery(convertAllSQLBlocks(queryBuilderForm.getQueryBlockList()));
            log.info("[queryTemplateForm]:{}", templateForm);
        }
        return result;
    }

    private String extractMemberIdFromUri(String requestURI) {
        String[] arr = requestURI.split("/");
        return arr[arr.length - 1];
    }

}
