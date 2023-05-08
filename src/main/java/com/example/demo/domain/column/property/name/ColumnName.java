package com.example.demo.domain.column.property.name;

import com.example.demo.util.validation.NamingUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.example.demo.util.validation.NamingUtils.*;


@Data
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
