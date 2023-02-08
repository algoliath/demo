package com.example.demo.domain.column.property.condition.value;

import lombok.Data;

import static com.example.demo.util.validation.TypeUtils.*;

@Data
public class ValueConditionTerm {

    private String argument;

    public ValueConditionTerm(){

    }

    public ValueConditionTerm(String argument) {
        if(isCharacter(argument)){
            argument = "'" + argument + "'";
        }
        this.argument = argument;
    }

}
