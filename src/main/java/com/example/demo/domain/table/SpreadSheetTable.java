package com.example.demo.domain.table;

import com.example.demo.domain.datasource.Source;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SpreadSheetTable {

    private List<List<Object>> tableElements;

    public SpreadSheetTable(List<List<Object>> elements){
        this.tableElements = new ArrayList<>(elements);
    }
    public SpreadSheetTable(Source<List<List<Object>>> source){
         this.tableElements = source.getData("spreadTableElements").get();
    }

    public List<Object> getColumn(String key){
        int index = tableElements.get(0).indexOf(key);
        if(index == -1){
            return new ArrayList<>();
        }
        return tableElements.stream()
                .map(row -> row.get(index))
                .collect(Collectors.toList());
    }

}
