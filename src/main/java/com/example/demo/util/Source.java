package com.example.demo.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class Source<T> {

    private final Map<String, T> paramMap = new ConcurrentHashMap<>();

    public Source(){

    }

    public Source(List<String> keys, List<T> values){
        int i = 0;
        for(String key: keys){
            add(key, values.get(i++));
        }
    }

    public void add(String key, T val){
        if(val != null){
            paramMap.put(key, val);
        }
        else{
            throw new IllegalArgumentException("null 값을 입력했습니다");
        }
    }
    public Optional<T> get(String key, T defaultValue){
        if(paramMap.get(key) == null){
            paramMap.put(key, defaultValue);
            return Optional.of(defaultValue);
        }
        return Optional.of(paramMap.get(key));
    }

    public Optional<T> get(String key){
        if(paramMap.get(key) == null){
            return Optional.empty();
        }
        return Optional.of(paramMap.get(key)
        );
    }

    public void clearMember(){
        paramMap.clear();
    }

    public void clearMember(String memberId){
        paramMap.remove(memberId);
    }

    public boolean isEmpty(){
        return paramMap.isEmpty();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for(String key: paramMap.keySet()){
            builder.append("{" + key + ":" + paramMap.get(key) + "} ");
            builder.append(",");
        }
        builder.setLength(builder.length()-1);
        builder.append("}");
        return builder.toString();
    }

}
