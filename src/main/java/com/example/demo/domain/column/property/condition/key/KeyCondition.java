package com.example.demo.domain.column.property.condition.key;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class KeyCondition {

    public static final KeyCondition PRIMARY_KEY_CONDITION = new KeyCondition(KeyConditionType.PRIMARY_KEY);
    public static final KeyCondition NOT_NULL = new KeyCondition(KeyConditionType.NOT_NULL);
    public static final KeyCondition UNIQUE = new KeyCondition(KeyConditionType.UNIQUE);

    private Long id;
    private KeyConditionType conditionType;

    public KeyCondition(){
    }

    public KeyCondition(KeyConditionType conditionType) {
        if(!KeyConditionType.contains(conditionType.toString())){
            throw new IllegalArgumentException("올바른 키 조건이 들어오지 않았습니다");
        }
        this.conditionType = conditionType;
    }

    public KeyCondition(String conditionType){
        if(!KeyConditionType.contains(conditionType)){
            throw new IllegalArgumentException("올바른 키 조건이 들어오지 않았습니다");
        }
        this.conditionType = KeyConditionType.valueOf(conditionType);
    }

    @Override
    public boolean equals(Object condition){
        KeyCondition keyCondition = (KeyCondition) condition;
        return this.conditionType == keyCondition.getConditionType();
    }

}
