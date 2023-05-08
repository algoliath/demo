package com.example.demo.domain.data.vo.template.query;

import com.example.demo.domain.data.vo.template.query.component.QueryBlock;
import com.example.demo.domain.sql.model.SQLBlockType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryBuilderForm {

    private List<QueryBlock> queryBlockList = new ArrayList<>();
    private String sqlError;
    private String sqlQuery;

    public QueryBuilderForm(){
        queryBlockList.add(new QueryBlock(SQLBlockType.SELECT, 0));
        queryBlockList.add(new QueryBlock(SQLBlockType.WHERE, 1));
        queryBlockList.add(new QueryBlock(SQLBlockType.GROUP_BY, 2));
        queryBlockList.add(new QueryBlock(SQLBlockType.HAVING, 3));
    }

    public QueryBlock getSQLBlock(int blockOrder){
        if(blockOrder >=0 && blockOrder < queryBlockList.size()){
            return queryBlockList.get(blockOrder);
        }
        throw new IllegalStateException(String.format("sql 블록 리스트의 인덱스 범위 {%s~%s}를 벗어났습니다", 0, queryBlockList.size()-1));
    }

    public void clear(){
        queryBlockList.clear();
    }

}
