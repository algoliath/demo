package com.example.demo.domain.column.property.condition.key;

import java.util.Arrays;

public enum KeyConditionType {

    PRIMARY_KEY, NOT_NULL, UNIQUE, NULL;

    private KeyConditionType(){

    }

    public static boolean contains(String candidate){
        return Arrays.stream(values()).anyMatch(keyConditionType -> keyConditionType.toString().equals(candidate));
    }

    public boolean match(String candidate){
        return contains(candidate) && valueOf(candidate) == this;
    }


}
