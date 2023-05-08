package com.example.demo.domain.data.vo.template.entity;

import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.template.TemplateForm;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.type.TemplateType;
import com.example.demo.util.Source;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class EntityTemplateForm extends TemplateForm {

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

    public void attachFile(String credentials, String spreadSheetTitle, String spreadSheetRange, Source<List<String>> errors){
        this.spreadSheetTitle = spreadSheetTitle;
        this.spreadSheetRange = spreadSheetRange;
        SpreadSheetSource spreadSheetSource = new SpreadSheetSource(credentials);
        Source<String> paramSource = new Source<>(Arrays.asList(SpreadSheetSource.FILE_NAME, SpreadSheetSource.FILE_RANGE), Arrays.asList(spreadSheetTitle, spreadSheetRange));
        try {
            SpreadSheetTable spreadSheetTable = spreadSheetSource.getSpreadSheetTable(paramSource).get(SpreadSheetSource.SPREAD_SHEET_VALUES).get();
            this.spreadSheetTable = spreadSheetTable;
        } catch (IOException e) {
            errors.get("spreadSheetRange", new ArrayList<>()).get().add("템플릿 스프레드시트 범위가 유효하지 않습니다");
        } catch (IllegalStateException e) {
            errors.get("spreadSheetRange", new ArrayList<>()).get().add("스프레드시트 파일에 값이 존재하지 않습니다");
        }
    }

    public void addJoinEntity(Entity entity) {
        joinTemplates.add(entity);
    }

    @Override
    public boolean isValidForm(){
        return super.isValidForm() && spreadSheetTable != null;
    }


}
