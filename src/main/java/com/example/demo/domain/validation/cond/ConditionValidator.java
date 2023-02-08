package com.example.demo.domain.validation.cond;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.columnTable.SpreadSheetTable;

import java.util.List;

@FunctionalInterface
public interface ConditionValidator {

    List<Integer> validate(SpreadSheetTable spreadSheetTable, Column column);

}
