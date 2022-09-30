package com.example.demo.domain.template;

import com.example.demo.domain.column.Column;

import java.util.Map;

public class Entity extends Template {

    public Entity(String name, Map<String, Column> columns, String memberId){
        super(name, TemplateType.ENTITY, columns, memberId);
    }

    @Override
    public Object convert() {
        return null;
    }

    @Override
    public boolean supports() {
        return false;
    }
}
