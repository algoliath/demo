
package com.example.demo.domain.column.property.condition.value;

import java.util.Arrays;

public enum ValueConditionType {

    GREATER_THAN(">"), LESS_THAN("<"), EQUALS("="), NULL("NULL"),
    LIKE("LIKE"),
    BEFORE("<"), AFTER(">"),
    FOREIGN_KEY("REFERENCES");

    String type;

    private ValueConditionType(String type){
        this.type = type;
    }

    public String getOracleSyntax(){
        return type;
    }

    public static boolean contains(String candidate){
        return Arrays.stream(values()).anyMatch(valueConditionType -> valueConditionType.toString().equals(candidate));
    }

    public boolean match(String candidate){
        return contains(candidate) && valueOf(candidate) == this;
    }

    public static ValueConditionType[] conditions(){
        return new ValueConditionType[]{GREATER_THAN, LESS_THAN, EQUALS, LIKE, BEFORE, AFTER};
    }

}
