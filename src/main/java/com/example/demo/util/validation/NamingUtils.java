package com.example.demo.util.validation;

public class NamingUtils {

    public static String removeCamelCase(String name){
        return name.trim().replaceAll("_", " ");
    }

    public static String addCamelCase(String name){
        return name.trim().replaceAll(" ", "_");
    }

    public static boolean isValid(String name){
        return isCamelCase(name) || isSingleton(name);
    }

    private static boolean isCamelCase(String name){
        boolean isUnderScore = false;
        for(char c: name.toCharArray()){
            if(!Character.isAlphabetic(c) && !Character.isDigit(c)){
                if(c != '_' ){
                    return false;
                }
                if(isUnderScore){
                    return false;
                }
                isUnderScore = true;
            }
        }
        return true;
    }

    private static boolean isSingleton(String name){
        for(char c: name.toCharArray()){
            if(!Character.isAlphabetic(c)){
                if(c != '_' ){
                    return false;
                }
            }
        }
        return true;
    }


    public static String parseString(String argument) {
        if(argument.length() < 2){
            throw new IllegalArgumentException("''로 둘러싸인 문자열이어야 합니다");
        }
        return argument.substring(1, argument.length()-1);
    }
}
