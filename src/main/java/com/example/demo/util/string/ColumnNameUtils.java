package com.example.demo.util.string;

public class ColumnNameUtils {

    public static boolean isValid(String name){
        return isCamelCase(name) || isSingleton(name);
    }

    private static boolean isCamelCase(String name){
        boolean isUnderScore = false;
        for(char c: name.toCharArray()){
            if(!Character.isAlphabetic(c)){
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

}
