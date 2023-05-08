package com.example.demo.domain.column;

import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.data.dto.ColumnDTO;
import com.example.demo.domain.data.dto.ConditionDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Column {

    private Long id;
    private String type;
    private ColumnName columnName;
    private List<KeyCondition> keyConditions = new ArrayList<>();
    private List<ValueCondition> valueConditions = new ArrayList<>();

    public Column(Long id, String type, ColumnName columnName){
        this.id = id;
        this.type = type;
        this.columnName = columnName;
    }

    public Column(ColumnUpdateForm columnUpdateForm) {
        this();
        this.columnName = new ColumnName(columnUpdateForm.getName());
        this.type = columnUpdateForm.getType();
        this.keyConditions = columnUpdateForm.getKeyConditions();
        this.valueConditions = columnUpdateForm.getValueConditions();
    }

    public Column(ColumnDTO columnDto, List<ConditionDTO> keyConditions, List<ConditionDTO> valueConditions){
        this.id = columnDto.getId();
        this.type = columnDto.getType();
        this.columnName = new ColumnName(columnDto.getName());
        for(ConditionDTO keyCondition: keyConditions){
            this.keyConditions.add(new KeyCondition(keyCondition.getType()));
        }
        for(ConditionDTO valueCondition: valueConditions){
            this.valueConditions.add(new ValueCondition(valueCondition.getType(), valueCondition.getTerm()));
        }
    }

    public boolean hasKeyCondition(KeyCondition keyCondition) {
        return keyConditions.contains(keyCondition);
    }

    public boolean hasValueCondition(ValueCondition valueCondition) {
        return valueConditions.contains(valueCondition);
    }

    public boolean hasValueConditionOtherThan(ValueConditionType target){
        return valueConditions.stream().filter(valueCondition -> valueCondition.getConditionType() != target).findAny().isPresent();
    }

    public Optional<ValueCondition> getValueCondition(ValueConditionType valueConditionType) {
        return valueConditions.stream().filter(valueCondition -> valueCondition.getConditionType() == valueConditionType).findAny();
    }

    @Override
    public String toString(){
        return columnName.getValidName();
    }

}
