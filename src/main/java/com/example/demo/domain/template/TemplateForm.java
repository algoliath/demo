package com.example.demo.domain.template;

import com.example.demo.domain.column.Column;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TemplateForm {

    @NotNull
    private String name;

    @NotNull
    private String type;

    private Map<String, Column> columns;

    public TemplateForm(){
        columns = new LinkedHashMap<>();
    }

}
