package com.example.demo.util.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ErrorsMappingUtils {

    public static boolean matchColumn(String code, String column){
        Pattern pattern = Pattern.compile("칼럼\\s\\S+\\의");
        Matcher matcher = pattern.matcher(code);
        while(matcher.find()){
            if(matcher.group().contains(column)){
                return true;
            }
        }
        return false;
    }


}
