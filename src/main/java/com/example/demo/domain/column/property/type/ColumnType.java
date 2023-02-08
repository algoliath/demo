package com.example.demo.domain.column.property.type;

import java.util.Locale;

import static com.example.demo.util.validation.TypeUtils.*;

public enum ColumnType {

    CHARACTER("VARCHAR", "CHARACTER"),
    NUMBER("INTEGER", "NUMBER"),
    DATE("DATE", "DATE"),
    NULL("NULL", "NULL");

    private String oracle_syntax;
    private String description;

    ColumnType(String oracle_syntax, String description){
        this.oracle_syntax = oracle_syntax;
        this.description = description;
    }

    public boolean match(String name){
        return this == valueOf(name);
    }

    public static String translate(String columnType, String key){
        switch(key){
            case "oracle" -> {
                return ColumnType.valueOf(columnType.toUpperCase(Locale.ROOT)).getOracleSyntax();
            }
        }
        return ColumnType.valueOf(columnType.toUpperCase(Locale.ROOT)).getDescription();
    }

    public static boolean contains(String value){
        return value != null && isCharacter(value) || isNumber(value) || isDate(value);
    }

    public String getOracleSyntax() {
        return oracle_syntax;
    }

    public String getDescription(){
        return description;
    }


}
