package com.example.demo.util.column;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.data.vo.template.QueryColumnUpdateForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ColumnUtils {

    public static boolean hasColumnName(List<Column> columns, Column targetColumn){
        return columns.stream().anyMatch(column -> column.equals(targetColumn));
    }

    public static boolean hasColumnName(List<QueryColumnUpdateForm> columns, QueryColumnUpdateForm targetColumn){
        boolean hasSuchColumnName =  columns.stream().anyMatch(column -> column.getName().equals(targetColumn.getName()));
        log.info("flag = {}", hasSuchColumnName);
        return columns.stream().anyMatch(column -> column.equals(targetColumn));
    }

}
