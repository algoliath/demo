package com.example.demo.domain.template.model;

import com.example.demo.domain.data.vo.template.QueryColumnUpdateForm;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Query extends Template{

    private String sheetTitle;
    private String sourceId;

    private List<QueryColumnUpdateForm> columns = new ArrayList<>();
    private List<QueryColumnUpdateForm> targetColumns = new ArrayList<>();
    private SpreadSheetTable sheetTable;
    private Boolean subQueryMark;

    public Query(){

    }

    public Query(EntityDTO entityDTO){
        super(entityDTO.getId(), entityDTO.getName(), TemplateType.ENTITY);
    }

    public Query(Template template, List<QueryColumnUpdateForm> columns){
        super(template.getId(), template.getName(), TemplateType.ENTITY);
        this.columns = columns;
    }

    public void addColumn(QueryColumnUpdateForm column) {
        columns.add(column);
    }

    public boolean hasColumn(String name){
        return columns.stream().anyMatch(queryColumnUpdateForm -> queryColumnUpdateForm.getName().equals(name));
    }
}
