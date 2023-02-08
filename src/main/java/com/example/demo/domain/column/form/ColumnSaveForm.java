package com.example.demo.domain.column.form;

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

    public boolean isEmpty(){
        return name == null || type == null;
    }

}

