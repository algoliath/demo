package com.example.demo.util.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtils {

    public static boolean isCharacter(String value){
        return !isNumber(value) && !isDate(value);
    }

    public static boolean isNumber(String str){
        str = str.replaceAll(" ", "");
        try{
            Integer.parseInt(str+"");
        } catch (NumberFormatException e){
            return false;
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
