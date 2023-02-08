package com.example.demo.util.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageConverter {

    public static String convertMessage(String code, Object[] argument){
        Pattern pattern = Pattern.compile("\\{\\S+\\}");
        Matcher matcher = pattern.matcher(code);
        StringBuilder builder = new StringBuilder();
        int start = 0;
        int k = 0;
        while(matcher.find()){
            System.out.println("found:" + matcher.group()
            );
            builder.append(code, start, matcher.start());
            builder.append(argument[k++]);
            start = matcher.end();
        }
        builder.append(code.substring(start));
        return builder.toString();
    }


}
