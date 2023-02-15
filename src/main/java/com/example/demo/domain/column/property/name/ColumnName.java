package com.example.demo.domain.column.property.name;

import com.example.demo.util.validation.NamingUtils;
import lombok.EqualsAndHashCode;

import static com.example.demo.util.validation.NamingUtils.*;


@EqualsAndHashCode
public class ColumnName {

    private String validName;

    public ColumnName(String validName){
        this.validName = addCamelCase(validName);
    }

    public String getValidName(){
        return validName;
    }

    @Override
    public String toString() {
        return validName;
    }
}
