package com.example.demo.domain.template.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Getter
public class Entity extends Template {

    private List<Column> columns = new ArrayList<>();
    private String sheetTitle;
    private String sheetRange;
    private String sourceId;
    private SpreadSheetTable sheetTable;


    public Entity(EntityTemplateForm templateForm){
        super(templateForm);
        this.sheetTitle = templateForm.getSpreadSheetTitle();
        this.sheetRange = templateForm.getSpreadSheetRange();
        this.sheetTable = templateForm.getSpreadSheetTable();
        this.columns = templateForm.getColumnUpdateForms().getColumnUpdateFormList()
                .stream().map(columnUpdateForm -> new Column(columnUpdateForm))
                .collect(Collectors.toList());
    }

    public Entity(EntityDTO entityDTO){
        super(entityDTO.getId(), entityDTO.getName(), TemplateType.ENTITY);
        this.sheetTitle = entityDTO.getFileName();
        this.sheetRange = entityDTO.getFileDomain();
    }

    public Entity() {

    }

    public void addColumn(Column column){
        columns.add(column);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Optional<Column> getPrimaryKeyColumn(){
        return columns.stream().filter(column -> column.getKeyConditions().contains(KeyCondition.PRIMARY_KEY_CONDITION)).findAny();
    }

    public String getSheetTitle() {
        return sheetTitle;
    }

    public String getSheetRange() {
        return sheetRange;
    }

}