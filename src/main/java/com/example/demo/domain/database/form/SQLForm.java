package com.example.demo.domain.database.form;

import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SQLForm {

    private List<SQLBlock> sqlBlockList = new ArrayList<>();
    private String sqlError;
    private String sqlQuery;

    public SQLForm(){
        sqlBlockList.add(new SQLBlock(SQLBlockType.SELECT, 0));
        sqlBlockList.add(new SQLBlock(SQLBlockType.WHERE, 1));
        sqlBlockList.add(new SQLBlock(SQLBlockType.GROUP_BY, 2));
        sqlBlockList.add(new SQLBlock(SQLBlockType.HAVING, 3));
    }

    public SQLBlock getSQLBlock(int blockOrder){
        if(blockOrder >=0 && blockOrder < sqlBlockList.size()){
            return sqlBlockList.get(blockOrder);
        }
        throw new IllegalStateException(String.format("sql 블록 리스트의 인덱스 범위 {%s~%s}를 벗어났습니다", 0, sqlBlockList.size()-1));
    }

    public void buildSQLQuery(){
        StringBuilder queryBuilder = new StringBuilder();
        int n_paragraph = 0;
        for(SQLBlock sqlBlock: sqlBlockList){
            if(sqlBlock.getSqlBlockType() == SQLBlockType.SELECT){
                n_paragraph++;
            }
            for(int i=1; i<n_paragraph; i++){
                queryBuilder.append(" ");
            }
            queryBuilder.append(sqlBlock.getSqlQuery());
            queryBuilder.append("\n");
        }
        this.sqlQuery = queryBuilder.toString();
    }

}
