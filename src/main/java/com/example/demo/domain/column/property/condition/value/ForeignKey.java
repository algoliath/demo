package com.example.demo.domain.column.property.condition.value;


import com.example.demo.domain.column.property.condition.options.OptionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignKey extends ValueCondition {

    private final OptionType updateType;
    private final OptionType deleteType;

    public ForeignKey(String referenceTarget, OptionType updateType, OptionType deleteType){
        super(ValueConditionType.FOREIGN_KEY, new ValueConditionTerm(referenceTarget));
        this.updateType = updateType;
        this.deleteType = deleteType;
    }
}
