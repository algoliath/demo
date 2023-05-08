package com.example.demo.domain.data.vo.template.query.component;

import com.example.demo.domain.sql.model.SQLBlockType;
import com.example.demo.domain.template.model.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class QueryBlockData {

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;
    private List<Query> queries = new ArrayList<>();
    private SQLBlockType SQLBlockType;

    public QueryBlockData() {


    }

    public QueryBlockData(Integer sqlBlockOrder, Integer sqlDataOrder) {
        this.sqlDataOrder = sqlDataOrder;
        this.sqlBlockOrder = sqlBlockOrder;
    }

    public QueryBlockData(SQLBlockType SQLBlockType, Integer sqlBlockOrder, Integer sqlDataOrder) {
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

}
