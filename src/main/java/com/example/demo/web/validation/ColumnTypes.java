package com.example.demo.web.validation;

import java.util.Locale;

public enum ColumnTypes {

    CHARACTER("VARCHAR"),
    NUMBER("INTEGER");

    private String entity;

    ColumnTypes(String description){
        this.entity = description;
    }

    public static String findDescription(String columnType){
        return ColumnTypes.valueOf(columnType.toUpperCase(Locale.ROOT)).entity;
    }

    public String getEntity() {
        return entity;
    }
}
