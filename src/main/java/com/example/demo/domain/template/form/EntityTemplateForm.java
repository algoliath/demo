package com.example.demo.domain.template.form;

import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class EntityTemplateForm extends TemplateForm{

    @NotBlank
    private String spreadSheetTitle;
    @NotBlank
    private String spreadSheetRange;
    private SpreadSheetTable spreadSheetTable;
    private String sourceId;
    private ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();
    private List<Entity> joinTemplates = new ArrayList<>();

    public EntityTemplateForm(){
    }

    public EntityTemplateForm(TemplateForm templateForm){
        super(templateForm.getName(), templateForm.getType());
    }

    public EntityTemplateForm(Entity entity) {
        super(entity.getName(), TemplateType.ENTITY.toString());
        this.columnUpdateForms = new ColumnUpdateForms(entity.getColumns());
        this.spreadSheetTitle = entity.getSheetTitle();
        this.spreadSheetRange = entity.getSheetRange();
        this.spreadSheetTable = entity.getSheetTable();
        this.sourceId = entity.getSourceId();
    }

}
