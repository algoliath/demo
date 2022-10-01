package com.example.demo.domain.template;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.table.SpreadSheetTable;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EntityTemplateForm extends TemplateForm{

    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private Map<String, Column> map;

    @NotNull
    private SpreadSheetTable spreadSheetTable;

    @NotNull
    private String spreadSheetTitle;

    @NotNull
    private String spreadSheetRange;

    public EntityTemplateForm(TemplateForm templateForm){
        name = templateForm.getName();
        type = templateForm.getType();
        map = templateForm.getColumns();
    }

}
