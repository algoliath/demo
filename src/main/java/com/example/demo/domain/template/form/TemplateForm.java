package com.example.demo.domain.template.form;

import com.example.demo.domain.column.Column;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TemplateForm {

//    @NotBlank
    private String name;
//    @NotBlank
    private String type;

    public TemplateForm(){
    }

    public TemplateForm(String name, String type){
        this.name = name;
        this.type = type;
    }

}
