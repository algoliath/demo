package com.example.demo.domain.datasource.parser;

import com.example.demo.domain.datasource.Source;
import com.example.demo.util.validation.StructuralElementUtils;
import com.google.api.services.docs.v1.model.StructuralElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DocsParser {

    public Map<Object, Map<String, String>> map(List<Source> sources){
        Map<Object, Map<String, String>> collect = sources.stream()
                .collect(Collectors.toMap(source -> source.getData("sourceId"),
                        source -> parse(source.getData("tableElement"))));
        return collect;
    }

    private Map<String, String> parse(Object element){
        StructuralElement tableElement = (StructuralElement) element;
        String content = StructuralElementUtils.readStructuralElements(Arrays.asList(tableElement));
        String[] split = content.split("\n");
        Map<String, String> sourceMap = new HashMap<>();
        for(int i=0; i+1<split.length; i+=2){
            sourceMap.put(split[i].trim(), split[i+1].trim());
        }
        return sourceMap;
    }

}
