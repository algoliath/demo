package com.example.demo.util.template;

import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.form.QueryTemplateForm;
import com.example.demo.domain.template.form.TemplateForm;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TemplateFormProvider {

    private Map<String, TemplateForm> templateMap = new ConcurrentHashMap<>();

    public void setTemplateForm(String memberUUID, TemplateForm templateForm){
        templateMap.put(memberUUID, templateForm);
    }

    public TemplateForm getTemplateForm(String memberId){
        templateMap.putIfAbsent(memberId, new TemplateForm());
        return templateMap.get(memberId);
    }

    public QueryTemplateForm getQueryTemplateForm(String memberId) {
        templateMap.putIfAbsent(memberId, new QueryTemplateForm());
        QueryTemplateForm queryTemplateForm = (QueryTemplateForm) getTemplateForm(memberId);
        return queryTemplateForm;
    }

    public EntityTemplateForm getEntityTemplateForm(String memberId) {
        templateMap.putIfAbsent(memberId, new EntityTemplateForm());
        EntityTemplateForm entityTemplateForm = (EntityTemplateForm) getTemplateForm(memberId);
        return entityTemplateForm;
    }

    public void clear(String memberId){
        templateMap.remove(memberId);
    }

}


