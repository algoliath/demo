package com.example.demo.web.template;

import com.example.demo.domain.template.EntityTemplateForm;
import com.example.demo.domain.template.TemplateForm;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TemplateFormProvider {

    private Map<String, TemplateForm> template_map = new ConcurrentHashMap<>();
    private EntityTemplateForm entityTemplateForm;

    public TemplateForm getTemplateForm(String memberId){
        template_map.putIfAbsent(memberId, new TemplateForm());
        return template_map.get(memberId);
    }

    public EntityTemplateForm getEntityTemplateForm(String memberId) {
        if(entityTemplateForm == null){
           entityTemplateForm = new EntityTemplateForm(getTemplateForm(memberId));
        }
        return entityTemplateForm;
    }

    public void clear(String memberKey){
        template_map.remove(memberKey);
    }
}
