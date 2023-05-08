package com.example.demo.domain.data.vo.template.query.component;

import com.example.demo.domain.sql.model.SQLBlockType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"sqlQuery", "dataHolder"})
public class QueryBlock {

    private SQLBlockType sqlBlockType;
    private String sqlQuery = "";
    private List<QueryBlockData> dataHolder = new ArrayList<>();
    private Integer order;

    public QueryBlock(){
        this.dataHolder.add(new QueryBlockData(order, 0));
    }

    public QueryBlock(Integer sqlBlockOrder) {
        this.dataHolder.add(new QueryBlockData(order, sqlBlockOrder));
        this.order = sqlBlockOrder;
    }

    public QueryBlock(SQLBlockType sqlBlockType, Integer sqlBlockOrder) {
        this.order = sqlBlockOrder;
        this.sqlBlockType = sqlBlockType;
        this.dataHolder.add(new QueryBlockData(sqlBlockType, order, sqlBlockOrder));
    }

    public void clear() {
        this.getDataHolder().clear();
        this.sqlBlockType = null;
        this.dataHolder.add(new QueryBlockData(order, 0));
    }

    public void addData(QueryBlockData queryBlockData){
        Integer dataOrder = queryBlockData.getSqlDataOrder();
        if(dataOrder == null){
            throw new IllegalStateException("dataOrder 입력 null 에러");
        }
        dataHolder.add(dataOrder, queryBlockData);
    }

    public void setData(QueryBlockData queryBlockData){
        Integer dataOrder = queryBlockData.getSqlDataOrder();
        if(dataOrder == null){
            throw new IllegalStateException("dataOrder 입력 null 에러");
        }
        dataHolder.set(dataOrder, queryBlockData);
    }

    public void changeSQLBlockType(SQLBlockType SQLBlockType){
        this.sqlBlockType = SQLBlockType;
        for(QueryBlockData queryBlockData : dataHolder){
            queryBlockData.setSQLBlockType(SQLBlockType);
        }
    }

    public void removeData(int sqlDataOrder) {
        dataHolder.remove(sqlDataOrder);
    }

    public QueryBlockData getSQLBlockData(int sqlBlockDataOrder){
        return dataHolder.get(sqlBlockDataOrder);
    }

}
