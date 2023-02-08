package com.example.demo.domain.database;

import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;

public enum SQLOperator {

    EXISTS("EXISTS"), ANY("ANY"), ALL("ALL"), SOME("SOME"),
    GREATER_THAN(">"), LESS_THAN("<"), EQUAL_TO("="), GREATER_THAN_OR_EQUAL(">="), IN("IN"), LIKE("LIKE");
    private String sign;

    SQLOperator(String sign){
        this.sign = sign;
    }

    public String getSign(){
        return this.sign;
    }

    public static boolean isKeyOperator(String operator) {
        if(Arrays.stream(values()).anyMatch(sqlOperator -> sqlOperator.match(operator))){
            return false;
        }
        SQLOperator sqlOperator = valueOf(operator);
        return sqlOperator == EXISTS || sqlOperator == ANY || sqlOperator == ALL || sqlOperator == SOME;
    }

    private boolean match(String operator) {
        return name().equals(operator);
    }

    public static SQLOperator[] mainQueryOperator(){
       return new SQLOperator[]{GREATER_THAN, LESS_THAN, EQUAL_TO, IN, LIKE};
    }

    public static SQLOperator[] subQueryOperator(){
        return new SQLOperator[]{EXISTS, ANY, ALL, SOME};
    }

}
