package com.example.demo.domain.column;

import java.util.Locale;

public enum ColumnType {

    CHARACTER("VARCHAR"),
    NUMBER("INTEGER"),
    DATE("DATE"),
    NULL("NULL");

    private String oracle;

    ColumnType(String oracle_description){
        this.oracle = oracle_description;
    }

    public static String translateOracle(String columnType){
        return ColumnType.valueOf(columnType.toUpperCase(Locale.ROOT)).oracle;
    }

    public static boolean match(String type, ColumnType columnType){
        return type.equals(columnType.name());
    }

    public static boolean matchAny(String type){
        for(ColumnType columnType: ColumnType.values()){
            if(columnType.name().equals(type)){
                return true;
            }
        }
        return false;
    }

    public String getOracle() {
        return oracle;
    }

}
