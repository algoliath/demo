package com.example.demo.domain.template.form;

import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.form.SQLForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Query;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class QueryTemplateForm extends TemplateForm{

    @NotBlank
    private String spreadSheetTitle;
    private SQLForm SQLForm = new SQLForm();
    private SpreadSheetTable spreadSheetTable;
    private String sourceId;
    private List<Entity> joinTemplates = new ArrayList<>();
    private List<SQLBlock> subQueryBlocks = new ArrayList<>();

    public QueryTemplateForm(){

    }

    public QueryTemplateForm(TemplateForm templateForm){
        super(templateForm.getName(), templateForm.getType());
    }

    public QueryTemplateForm(Query query) {
        super(query.getName(), TemplateType.QUERY.toString());
    }

    public SQLBlock getSQLBlock(int order){
        return SQLForm.getSqlBlockList().get(order);
    }

}
