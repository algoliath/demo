package com.example.demo.domain.database.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"sqlQuery", "dataHolder"})
public class SQLBlock {

    private SQLBlockType sqlBlockType;
    private List<SQLBlockData> dataHolder = new ArrayList<>();
    private Integer order;
    private String sqlQuery = "";

    public SQLBlock(){
        this.dataHolder.add(new SQLBlockData(order, 0));
    }

    public SQLBlock(Integer sqlBlockOrder) {
        this.dataHolder.add(new SQLBlockData(order, sqlBlockOrder));
        this.order = sqlBlockOrder;
    }

    public SQLBlock(SQLBlockType sqlBlockType, Integer sqlBlockOrder) {
        this.order = sqlBlockOrder;
        this.sqlBlockType = sqlBlockType;
        this.dataHolder.add(new SQLBlockData(sqlBlockType, order, sqlBlockOrder));
    }

    public void clear() {
        this.getDataHolder().clear();
        this.sqlBlockType = null;
        this.dataHolder.add(new SQLBlockData(order, 0));
    }

    public void addData(SQLBlockData sqlBlockData){
        Integer dataOrder = sqlBlockData.getSqlDataOrder();
        if(dataOrder == null){
            throw new IllegalStateException("dataOrder 입력 null 에러");
        }
        dataHolder.add(dataOrder, sqlBlockData);
    }

    public void setData(SQLBlockData sqlBlockData){
        Integer dataOrder = sqlBlockData.getSqlDataOrder();
        if(dataOrder == null){
            throw new IllegalStateException("dataOrder 입력 null 에러");
        }
        dataHolder.set(dataOrder, sqlBlockData);
    }


}
