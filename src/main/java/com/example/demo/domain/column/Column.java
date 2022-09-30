package com.example.demo.domain.column;

import lombok.Data;

@Data
public class Column {

    private String name;
    private String type;

    public Column(){

    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

}
