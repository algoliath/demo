package com.example.demo.domain.column.property.condition.value;


import com.example.demo.domain.column.property.condition.options.OptionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignKey extends ValueCondition {

    private final String referenceColumn;
    private final OptionType updateMode;
    private final OptionType deleteMode;

    public ForeignKey(String referenceTarget, String referenceColumn, OptionType updateMode, OptionType deleteMode){
        super(ValueConditionType.FOREIGN_KEY, new ValueConditionTerm(referenceTarget));
        this.referenceColumn = referenceColumn;
        this.updateMode = updateMode;
        this.deleteMode = deleteMode;
    }
}
