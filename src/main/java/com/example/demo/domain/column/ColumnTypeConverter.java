package com.example.demo.domain.column;

import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.util.validation.TypeUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.util.validation.TypeUtils.*;

@Component
public class ColumnTypeConverter {

    public ColumnType getType(List<Object> column) {
        if(column.stream().allMatch(row -> isDate(String.valueOf(row)))){
            return ColumnType.DATE;
        }
        if(column.stream().allMatch(row -> isNumber(String.valueOf(row)))){
            return ColumnType.NUMBER;
        }
        return ColumnType.CHARACTER;
    }

    public Map<String, ColumnType> batchGetMap(List<List<Object>> columns){
        return columns.stream().collect(Collectors.toMap(column -> column.get(0).toString(), column -> getType(column.subList(1, column.size()))));
    }

    public List<ColumnType> batchGetList(List<List<Object>> columns){
        return columns.stream().map(column -> getType(column)).collect(Collectors.toList());
    }

}
