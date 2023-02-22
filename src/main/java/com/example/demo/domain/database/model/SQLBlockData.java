package com.example.demo.domain.database.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.database.SQLOperator;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Query;
import com.example.demo.util.validation.QueryUtils;
import lombok.Data;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class SQLBlockData {

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;
    private List<Query> queries = Arrays.asList();
    private SQLBlockType SQLBlockType;

    public SQLBlockData() {


    }

    public SQLBlockData(Integer sqlBlockOrder, Integer sqlDataOrder) {
        this.sqlDataOrder = sqlDataOrder;
        this.sqlBlockOrder = sqlBlockOrder;
    }

    public SQLBlockData(SQLBlockType SQLBlockType, Integer sqlBlockOrder, Integer sqlDataOrder) {
        this(sqlBlockOrder, sqlDataOrder);
        this.SQLBlockType = SQLBlockType;
    }

    public Query getQuery(int order){
        if(order < 0 || order >= queries.size()){
            throw new IllegalArgumentException(String.format("인덱스 범위는 %d과 %d 사이에 있어야 합니다", 0, queries.size()));
        }
        return queries.get(order);
    }

    public Optional<Query> getQuery(String templateName){
        return queries.stream().filter(query -> query.getName().equals(templateName)).findAny();
    }

    public void addQuery(Query query){
        queries.add(query);
    }


    public void deleteQuery(String queryName){
        Optional<Query> targetOptional = queries.stream().filter(query -> query.getName().equals(queryName)).findAny();
        if(!targetOptional.isEmpty()){
            queries.remove(targetOptional.get());
        }
    }

    @Override
    public String toString() {
        if (SQLBlockType == null) {
            return "MISSING";
        }
        return QueryUtils.convertSQLBlockData(queries, SQLBlockType);
    }
}
