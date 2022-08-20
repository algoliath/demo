package com.example.demo.domain.form;

import com.example.demo.domain.template.ColumnName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ColumnSaveForm {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String type;


    public ColumnSaveForm(){

    }

    public ColumnSaveForm(String name, String type){
        this.name = name;
        this.type = type;
    }

    public boolean isEmpty(){
        return this.name == null || this.type == null;
    }

}
