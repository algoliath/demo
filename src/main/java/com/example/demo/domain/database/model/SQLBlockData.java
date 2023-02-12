package com.example.demo.domain.database.model;

import com.example.demo.domain.database.SQLOperator;
import com.example.demo.util.validation.QueryUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SQLBlockData {

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;
    private String templateName;
    private List<String> targetColumns = new ArrayList<>();
    private List<String> columns = new ArrayList<>();
    private String operator;
    private String operand;
    private SQLBlockType sqlBlockType;

    public SQLBlockData(){

    }

    public SQLBlockData(Integer sqlBlockOrder, Integer sqlDataOrder) {
        this.sqlDataOrder = sqlDataOrder;
        this.sqlBlockOrder = sqlBlockOrder;
    }

    public SQLBlockData(SQLBlockType sqlBlockType, Integer sqlBlockOrder, Integer sqlDataOrder){
        this(sqlBlockOrder, sqlDataOrder);
        this.sqlBlockType = sqlBlockType;
    }


    @Override
    public String toString() {
        if(sqlBlockType == null){
            return "";
        }
        switch(sqlBlockType){
            case SELECT -> {
                return targetColumns.stream().map(column ->  getTemplateName() + "." + column).collect(Collectors.joining(", "));
            }
            case GROUP_BY -> {
                return templateName;
            }
            case WHERE, HAVING -> {
                return targetColumns.stream().map(column ->  getTemplateName() + "." + column + " " + SQLOperator.valueOf(operator).getSign() + " " + getOperand()).collect(Collectors.joining(""));
            }
            case JOIN -> {
                return targetColumns.stream().map(column ->  getTemplateName() + "." + column).collect(Collectors.joining(""));
            }
            case SUBQUERY -> {
                return getOperator() + "(" + getOperand() + ")";
            }
            default -> {
                return null;
            }
        }
    }

    public String getTargetColumn(int i) {
        return targetColumns.get(i);
    }
}
