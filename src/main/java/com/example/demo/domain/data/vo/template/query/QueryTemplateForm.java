package com.example.demo.domain.data.vo.template.query;

import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.template.TemplateForm;
import com.example.demo.domain.data.vo.template.query.component.QueryBlock;
import com.example.demo.domain.template.model.Query;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QueryTemplateForm extends TemplateForm {

    @NotBlank
    private String templateName;
    private String spreadSheetTitle;
    private String spreadSheetRange;
    private SpreadSheetTable spreadSheetTable;
    private String sourceId;
    private QueryBuilderForm queryBuilderForm = new QueryBuilderForm();

    public QueryTemplateForm(){

    }

    public QueryTemplateForm(TemplateForm templateForm){
        super(templateForm.getName(), templateForm.getType());
    }

    public QueryTemplateForm(Query query) {
        super(query.getName(), TemplateType.QUERY.toString());
    }

    public QueryBlock getSQLBlock(int order){
        return queryBuilderForm.getQueryBlockList().get(order);
    }

}
