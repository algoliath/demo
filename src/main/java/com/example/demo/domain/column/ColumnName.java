package com.example.demo.domain.column;

import com.example.demo.util.validation.ColumnUtils;

public class ColumnName {

    String name;

    public ColumnName(String name){
        if(!ColumnUtils.isValid(name)){
           throw new IllegalArgumentException("칼럼 이름은 camelCase 또는 singleton 이어야 합니다");
        }
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
