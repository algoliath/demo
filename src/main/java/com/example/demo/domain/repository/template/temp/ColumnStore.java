package com.example.demo.domain.repository.template.temp;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.template.model.Template;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ColumnStore {


    Map<Template, List<Column>> map = new ConcurrentHashMap<>();

    public void save(Template template, Column column){
        map.putIfAbsent(template, new ArrayList<>());
        map.get(template).add(column);
    }

    public void saveBatch(Template template, Collection<Column> columns){
        map.putIfAbsent(template, new ArrayList<>());
        for(Column column: columns){
            map.get(template).add(column);
        }
    }
}
