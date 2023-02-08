package com.example.demo.domain.columnTable;

import com.example.demo.domain.column.Column;

import com.example.demo.domain.column.property.name.ColumnName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
public class SpreadSheetTable {

    private String spreadSheetRange;
    private List<List<Object>> values;

    public SpreadSheetTable(){

    }

    public SpreadSheetTable(String spreadSheetRange, List<List<Object>> values){
        this.values = preprocess(values);
        this.spreadSheetRange = spreadSheetRange;
    }

    public List<Object> getRow(int index){
        return values.get(index);
    }

    public List<List<Object>> getRows(){
        return this.values;
    }

    public List<Object> getColumn(Column column){
        int index = values.get(0).indexOf(column.getColumnName().getValidName());
        if(index == -1){
            log.info("column not found");
            return new ArrayList<>();
        }
        return values.subList(1, values.size()).stream()
                .map(row -> row.get(index))
                .collect(Collectors.toList());
    }

    public List<Object> getColumn(String name, boolean inclusive){
        log.info("name={}", name);
        int column_index = values.get(0).indexOf(name);
        int start_index = inclusive? 0:1;
        if(column_index == -1){
            return new ArrayList<>();
        }
        return values.subList(start_index, values.size()).stream()
                .map(row -> row.get(column_index))
                .collect(Collectors.toList());
    }

    public List<List<Object>> getColumns(boolean inclusive){
        List<Object> columns = getRow(0);
        return columns.stream().map(column -> getColumn(column.toString(), inclusive)).collect(Collectors.toList());
    }

    public String getColumnRange(String columnName){
        char columnOrder = (char)('A' + getRow(0).indexOf(columnName));
        String rangeStart = columnOrder + "" + spreadSheetRange.charAt(spreadSheetRange.indexOf(':')-1);
        String rangeEnd = columnOrder + "" + spreadSheetRange.charAt(spreadSheetRange.length()-1);
        log.info("columnOrder of {}={}", columnName, getRow(0).indexOf(columnName));
        return rangeStart + ":" + rangeEnd;
    }

    public String getCellRange(String columnName, Object columnValue){
        int rowOrder = getColumn(columnName, false).indexOf(columnValue);
        char columnOrder = (char)('A' + values.get(0).indexOf(columnName));
        return columnOrder + "" + rowOrder;
    }

    public int getColumnIndex(String columnName) {
        return values.get(0).indexOf(columnName);
    }

    public String getSheetName(){
        return spreadSheetRange.substring(0, spreadSheetRange.indexOf('!')) + "!";
    }

    public boolean hasDuplicateRow(int row){
        Set<Object> set = new HashSet<>();
        for(Object value: getRow(row)){
            if(!set.add(value)){
                return true;
            }
        }
        return false;
    }
    public boolean hasDuplicateColumn(String column){
        Set<Object> set = new HashSet<>();
        for(Object value: getColumn(column, false)){
            if(!set.add(value)){
                return true;
            }
        }
        return false;
    }
    public List<List<Object>> preprocess(List<List<Object>> values){
        List<List<Object>> result = new ArrayList<>();
        if(values.size() == 0){
            throw new IllegalArgumentException();
        }
        result.add(new ArrayList<>());
        for(Object value: values.get(0)){
            ColumnName columnName = new ColumnName((String) value);
            result.get(0).add(columnName.getValidName());
        }
        for(int i=1; i<values.size(); i++){
            result.add(new ArrayList<>());
            for(Object value: values.get(i)){
                result.get(i).add(value);
            }
        }
        return result;
    }

}
