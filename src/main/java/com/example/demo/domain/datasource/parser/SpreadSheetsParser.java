package com.example.demo.domain.datasource.parser;

import com.example.demo.domain.datasource.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SpreadSheetsParser {

    public List<List<Object>> parse(List<Source> sources){
        log.info("[sources]={}", sources);
        return sources.stream()
                .map(source -> (List<List<Object>>) (source.getData("spreadTableElement")).orElse(new ArrayList<>()))
                .findFirst().orElse(new ArrayList<>());
    }


}
