package com.example.demo.domain.template;

import com.example.demo.domain.column.Column;
import lombok.Data;

import java.util.Map;

@Data
public class Template {

    private Column keyColumn;

    private Long id;

    private final String name;

    private final Map<String, Column> columns;

    private final TemplateType type;

    private final String memberId;

    public Template(String name, TemplateType type, Map<String, Column> columns, String memberId) {
        this.name = name;
        this.columns = columns;
        this.type = type;
        this.memberId = memberId;
    }

    public Object convert(){
        return null;
    }

    public boolean supports(){
        return false;
    }

}
