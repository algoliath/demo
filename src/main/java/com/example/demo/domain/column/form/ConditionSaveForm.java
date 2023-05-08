package com.example.demo.domain.column.form;

import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import lombok.Data;

// separate into keyConditionSaveForm and valueConditionSaveForm
@Data
public class ConditionSaveForm {

    private String columnName;
    private String keyConditionType;
    private String valueConditionType;
    private String valueConditionTerm;

    public ConditionSaveForm(){

    }

    public ConditionSaveForm(String conditionType, String conditionTerm){
        this.valueConditionType = conditionType;
        this.valueConditionTerm = conditionTerm;
    }

    public boolean isValidKeyCondition(){
        return KeyConditionType.contains(keyConditionType);
    }

    public boolean isValidValueCondition() {
        return ValueConditionType.contains(valueConditionType) && ColumnType.contains(valueConditionTerm);
    }

}
