package com.example.demo.domain.column.property.condition.value;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"id", "conditionTerm"})
public class ValueCondition {

    private Long id;
    public static final ValueCondition FOREIGN_KEY_CONDITION = new ValueCondition(ValueConditionType.FOREIGN_KEY, null);
    private ValueConditionType conditionType;
    private ValueConditionTerm conditionTerm;

    public ValueCondition(){

    }

    public ValueCondition(ValueConditionType conditionType, ValueConditionTerm conditionTerm) {
        this();
        this.conditionType = conditionType;
        this.conditionTerm = conditionTerm;
    }

    public ValueCondition(String conditionType, String conditionTerm){
        this(ValueConditionType.valueOf(conditionType), new ValueConditionTerm(conditionTerm));
    }

    @Override
    public boolean equals(Object condition){
        ValueCondition valueCondition = (ValueCondition) condition;
        return this.conditionType == valueCondition.getConditionType();
    }


}
