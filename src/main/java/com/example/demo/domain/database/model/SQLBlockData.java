package com.example.demo.domain.database.model;

import com.example.demo.domain.database.SQLOperator;
import lombok.Data;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SQLBlockData {

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;
    private String templateName;
    private String templateAlias;
    private List<String> targetColumns = new ArrayList<>();
    private List<String> columns = new ArrayList<>();
    private String operator;
    private String operand;
    private SQLBlockType SQLBlockType;

    public SQLBlockData() {

    }

    public SQLBlockData(Integer sqlBlockOrder, Integer sqlDataOrder) {
        this.sqlDataOrder = sqlDataOrder;
        this.sqlBlockOrder = sqlBlockOrder;
    }

    public SQLBlockData(SQLBlockType SQLBlockType, Integer sqlBlockOrder, Integer sqlDataOrder) {
        this(sqlBlockOrder, sqlDataOrder);
        this.SQLBlockType = SQLBlockType;
    }


    @Override
    public String toString() {

        if (SQLBlockType == null) {
            return "MISSING";
        }

        switch (SQLBlockType) {
            case SELECT -> {
                if (templateAlias != null) {
                    return targetColumns.stream().map(column -> getTemplateAlias() + "." + column).collect(Collectors.joining(", "));
                }
                return targetColumns.stream().map(column -> templateName + "." + column).collect(Collectors.joining(", "));
            }
            case GROUP_BY -> {
                return targetColumns.stream().collect(Collectors.joining(","));
            }
            case WHERE, HAVING -> {
                if (templateAlias != null){
                    return targetColumns.stream().map(column -> operator + "(" + templateName + ")").collect(Collectors.joining(" AND "));
                }
                return targetColumns.stream().filter(column -> SQLOperator.hasOperator(getOperator())).map(column -> getTemplateName() + "." + column + " " + SQLOperator.valueOf(operator).getSign() + " " + getOperand()).collect(Collectors.joining(" AND "));
            }
            case JOIN -> {
                return targetColumns.stream().map(column -> templateName + "." + column).collect(Collectors.joining(""));
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
