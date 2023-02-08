package com.example.demo.domain.column.form;

import com.example.demo.domain.column.property.condition.options.OptionType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;

@Data
public class JoinConditionSaveForm extends ConditionSaveForm{

    @NotBlank
    private String columnName;
    @NotBlank
    private String templateName;
    @NotBlank
    private String joinColumn;
    private String updateMode;
    private String deleteMode;
    public JoinConditionSaveForm(){

    }

    public JoinConditionSaveForm(String valueConditionType, String valueConditionTerm, String columnName, String updateMode, String deleteMode){
        super(valueConditionType, valueConditionTerm);
        this.columnName = columnName;
        this.updateMode = updateMode;
        this.deleteMode = deleteMode;
    }

    public boolean isValid(){
        return columnName!=null && templateName!=null && Arrays.stream(OptionType.values())
                .filter(optionType -> optionType.match(updateMode) || optionType.match(deleteMode))
                .findAny().isPresent();
    }

}
