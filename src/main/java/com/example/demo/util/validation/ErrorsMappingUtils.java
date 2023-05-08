package com.example.demo.util.validation;

import com.example.demo.util.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public static boolean containsColumn(Source<List<String>> errors, String column){
        Pattern pattern = Pattern.compile("칼럼\\s\\S+\\의");
        for(List<String> errCodes: errors.getParamMap().values()){
            for(String errCode: errCodes){
                Matcher matcher = pattern.matcher(errCode);
                while(matcher.find()){
                    if(matcher.group().contains(column)){
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
