package com.example.demo.domain.column;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ColumnTypeClassifier {

    public static ColumnType process(String value){
        for(char c: value.toCharArray()){
            if(isCharacter(value)){
                return ColumnType.CHARACTER;
            }
            if(isDigit(value)){
                return ColumnType.NUMBER;
            }
            if(isDate(value)){
                return ColumnType.DATE;
            }
        }
        return ColumnType.NULL;
    }

    public ColumnType processBatch(List<Object> column) {
        if(column.stream().allMatch(row -> isDate(String.valueOf(row)))){
            return ColumnType.DATE;
        }
        if(column.stream().allMatch(row -> isDigit(String.valueOf(row)))){
            return ColumnType.NUMBER;
        }
        if(column.stream().allMatch(row -> isCharacter(String.valueOf(row)))){
            return ColumnType.CHARACTER;
        }
        return ColumnType.NULL;
    }

    public static boolean isCharacter(String value){
        value = value.replaceAll(" ", "");
        for(char c: value.toCharArray()){
            if(!Character.isAlphabetic(c) && !Character.isDigit(c)){
               return false;
            }
        }
        return true;
    }

    public static boolean isDigit(String value){
        value = value.replaceAll(" ", "");
        for(char c: value.toCharArray()){
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    public static boolean isDate(String value){
        value = value.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


}
