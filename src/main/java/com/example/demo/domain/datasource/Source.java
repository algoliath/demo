package com.example.demo.domain.datasource;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Source<T> {

    private Map<String, T> paramMap = new ConcurrentHashMap<>();

    public void setData(String key, T val){
        paramMap.put(key, val);
    }

    public Optional<T> getData(String key){
        return Optional.of(paramMap.get(key));
    }

    public void clear(){
        paramMap.clear();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(String key: paramMap.keySet()){
            builder.append("{" + key + ":" + paramMap.get(key) + "}");
        }
        return builder.toString();
    }

}
