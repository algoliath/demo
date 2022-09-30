package com.example.demo.domain.column.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ColumnUpdateForm {

    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private String mode;
    @NotNull
    private String lastName;

    public ColumnUpdateForm(){

    }

    public ColumnUpdateForm(String name, String type, String mode, String lastName){
        this.name = name;
        this.type = type;
        this.mode = mode;
        this.lastName = lastName;
    }

}
