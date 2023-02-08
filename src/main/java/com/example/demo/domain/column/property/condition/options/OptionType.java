package com.example.demo.domain.column.property.condition.options;

import java.util.Arrays;

public enum OptionType {

    NO_ACTION, SET_DEFAULT_NULL, CASCADE;

    public static boolean contains(String candidate){
        return Arrays.stream(values()).anyMatch(optionType -> optionType.toString().equals(candidate));
    }

    public boolean match(String candidate){
        return contains(candidate) && valueOf(candidate) == this;
    }

}
