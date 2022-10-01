package com.example.demo.domain.template;

public enum TemplateType {

    TABLE(), ENTITY();

    TemplateType(){

    }

    public static boolean match(TemplateType templateType, TemplateType targetType){
        return templateType.equals(targetType);
    }

    public static boolean match(String templateType, TemplateType targetType){
        return targetType.name().equals(templateType);
    }

}
