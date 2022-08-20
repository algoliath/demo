package com.example.demo.domain.template;

import lombok.Data;

@Data
public class Column {

    private ColumnName name;
    private String type;

    public Column(){

    }

    public Column(ColumnName name, String type) {
        this.name = name;
        this.type = type;
    }


}
