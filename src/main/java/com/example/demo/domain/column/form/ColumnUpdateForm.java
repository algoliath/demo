package com.example.demo.domain.column.form;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.data.dto.ColumnDTO;
import com.example.demo.domain.data.dto.ConditionDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(exclude = {"type","keyConditions","valConditions"})
public class ColumnUpdateForm {

    @NotNull
    private String name;
    @NotNull
    private String type;
    private List<KeyCondition> keyConditions = new ArrayList<>();
    private List<ValueCondition> valueConditions = new ArrayList<>();
    private ForeignKey foreignKey;

    public ColumnUpdateForm(){
    }

    public ColumnUpdateForm(String name, String type){
        this.name = name;
        this.type = type;
    }

    public ColumnUpdateForm(ColumnName nameWrapper, ColumnType typeWrapper){
        this.name = nameWrapper.getValidName();
        this.type = typeWrapper.toString();
    }

    public ColumnUpdateForm(Column column){
        this.name = column.getColumnName().getValidName();
        this.type = column.getType();
        this.keyConditions = column.getKeyConditions();
        this.valueConditions = column.getValueConditions();
    }

    public ColumnUpdateForm(ColumnDTO columnDto, List<ConditionDTO> keyConditions, List<ConditionDTO> valueConditions){
        this.name = columnDto.getName();
        this.type = columnDto.getType();
        for(ConditionDTO keyCondition: keyConditions){
            this.keyConditions.add(new KeyCondition(keyCondition.getType()));
        }
        for(ConditionDTO valueCondition: valueConditions){
            this.valueConditions.add(new ValueCondition(valueCondition.getType(), valueCondition.getTerm()));
        }
    }

    public void addKeyCondition(KeyCondition keyCondition){
        keyConditions.add(keyCondition);
    }

    public void addValueCondition(ValueCondition valueCondition){
        valueConditions.add(valueCondition);
    }

    public void deleteKeyCondition(KeyCondition keyCondition) {
        keyConditions.remove(keyCondition);
    }

    public void deleteValueCondition(ValueCondition valueCondition) {
        valueConditions.remove(valueCondition);
    }

    @Override
    public boolean equals(Object o){
        ColumnUpdateForm columnUpdateForm = (ColumnUpdateForm) o;
        return this.name == columnUpdateForm.getName();
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.name);
    }

}
