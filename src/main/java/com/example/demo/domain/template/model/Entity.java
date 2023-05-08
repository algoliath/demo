package com.example.demo.domain.template.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.template.type.TemplateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Getter
@Setter
@EqualsAndHashCode(exclude = {"sourceId"})
public class Entity extends Template {

    private String sheetTitle;
    private String sheetRange;
    private SpreadSheetTable sheetTable;
    private List<Column> columns = new ArrayList<>();
    private String sourceId;


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

    public static Entity createEntity(Template template, List<Column> columns){
        Entity entity = new Entity();
        entity.setId(template.getId());
        entity.setName(template.getName());
        entity.setTemplateType(template.getTemplateType());
        entity.setColumns(columns);
        return entity;
    }

    public void addColumn(Column column){
        columns.add(column);
    }


    public Optional<Column> getPrimaryKeyColumn(){
        return columns.stream().filter(column -> column.getKeyConditions().contains(KeyCondition.PRIMARY_KEY_CONDITION)).findAny();
    }


}
