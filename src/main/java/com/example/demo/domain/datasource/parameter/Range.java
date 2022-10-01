package com.example.demo.domain.datasource.parameter;

import lombok.Data;

@Data
public class Range {

    String ranges;

    public Range(String ranges){

        this.ranges = ranges;

        String[] split = ranges.split("!")[1].split(":");

        if(isValidInput(split[0]) || isValidInput(split[1])){
            throw new IllegalArgumentException("입력하신 범위가 올바른 형식이 아닙니다 (예시: A1:B3)");
        }

    }

    private boolean isValidInput(String range){
        range = range.replaceAll(" ", "");
        if(range.length()<1){
            return false;
        }
        if(range.length()<2){
            return Character.isAlphabetic(range.charAt(0));
        }
        return Character.isAlphabetic(range.charAt(0)) && Character.isDigit(range.charAt(1));
    }

    @Override
    public String toString(){
        return this.ranges;
    }

}
