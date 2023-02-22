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
    private String operator;
    private String operand;
    private String sourceId;

    private List<Column> columns = new ArrayList<>();
    private List<Column> targetColumns = new ArrayList<>();
    private SpreadSheetTable sheetTable;
    private Boolean isSubQuery;

}
