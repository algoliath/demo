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

    public void setQueryTemplateForm(String memberUUID, QueryTemplateForm queryTemplateForm){
        templateMap.put(memberUUID, queryTemplateForm);
    }

    public void setEntityTemplateForm(String memberUUID, EntityTemplateForm entityTemplateForm){
        templateMap.put(memberUUID, entityTemplateForm);
    }

    public TemplateForm getTemplateForm(String memberId){
        templateMap.putIfAbsent(memberId, new TemplateForm());
        return templateMap.get(memberId);
    }

    public QueryTemplateForm getQueryTemplateForm(String memberUUID) {
        if(!(templateMap.get(memberUUID) instanceof QueryTemplateForm)){
            templateMap.clear();
        }
        templateMap.putIfAbsent(memberUUID, new QueryTemplateForm());
        QueryTemplateForm queryTemplateForm = (QueryTemplateForm) getTemplateForm(memberUUID);
        return queryTemplateForm;
    }

    public EntityTemplateForm getEntityTemplateForm(String memberUUID) {
        if(!(templateMap.get(memberUUID) instanceof EntityTemplateForm)){
            templateMap.clear();
        }
        templateMap.putIfAbsent(memberUUID, new EntityTemplateForm());
        EntityTemplateForm entityTemplateForm = (EntityTemplateForm) getTemplateForm(memberUUID);
        return entityTemplateForm;
    }

    public void clear(String memberId){
        templateMap.remove(memberId);
    }

}


