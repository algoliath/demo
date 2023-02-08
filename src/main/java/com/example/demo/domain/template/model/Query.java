package com.example.demo.domain.template.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.template.form.QueryTemplateForm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Query extends Template{

    private String sheetTitle;
    private SpreadSheetTable sheetTable;
    private String sourceId;

    private List<Column> verticalColumns = new ArrayList<>();
    private List<Column> horizontalColumns = new ArrayList<>();

    public Query(QueryTemplateForm queryTemplateForm){
        super(queryTemplateForm);
        this.sheetTitle = queryTemplateForm.getSpreadSheetTitle();
        this.sourceId = queryTemplateForm.getSourceId();
    }

}
