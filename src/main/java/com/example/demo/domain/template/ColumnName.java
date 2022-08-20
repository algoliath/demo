package com.example.demo.domain.template;

import com.example.demo.util.string.ColumnNameUtils;

public class ColumnName {

    String name;

    public ColumnName(String name){
        if(!ColumnNameUtils.isValid(name)){
           throw new IllegalArgumentException("칼럼 이름은 camelCase 또는 singleton 이어야 합니다");
        }
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
