package com.example.demo.domain.template.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.template.form.TemplateForm;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Template {

    private Long id;
    private String uuid;
    private String name;
    private TemplateType templateType;

    public Template(){
        uuid = UUID.randomUUID().toString();
    }

    public Template(TemplateForm templateForm) {
        this.name = templateForm.getName();
        this.templateType = TemplateType.valueOf(templateForm.getType());
    }

    public Template(Long id, String name, TemplateType templateType) {
        this.id = id;
        this.templateType = templateType;
        this.name = name;
    }
}

