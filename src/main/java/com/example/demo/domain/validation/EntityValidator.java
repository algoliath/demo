package com.example.demo.domain.validation;

import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.ColumnTypeConverter;
import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.util.Source;
import com.example.demo.util.message.MessageConverter;
import com.example.demo.util.validation.NamingUtils;
import com.example.demo.util.validation.QueryUtils;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.domain.validation.ErrorConst.*;

@Slf4j
@Component
@Qualifier("entityValidator")
@RequiredArgsConstructor
public class EntityValidator {

    private final ColumnTypeConverter converter;

    public void validate(Object target, Source<List<String>> bindingResult) {

        if(!EntityTemplateForm.class.isAssignableFrom(target.getClass())){
            return;
        }

        EntityTemplateForm templateForm = (EntityTemplateForm) target;
        ColumnUpdateForms columnUpdateForms = templateForm.getColumnUpdateForms();
        List<ColumnUpdateForm> columnUpdateFormList = templateForm.getColumnUpdateForms().getColumnUpdateFormList();
        SpreadSheetTable spreadSheetTable = templateForm.getSpreadSheetTable();

        if(spreadSheetTable == null){
            return;
        }

        String columnName;
        String expectedType;

        if(spreadSheetTable.hasDuplicateRow(0)){
            String message = "스프레드시트 테이블의 칼럼은 중복될 수 없습니다";
            bindingResult.get("duplicateColumn.template.spreadSheetTable",new ArrayList<>()).get().add(message);
        }

        if(columnUpdateForms.hasDuplicateColumns()) {
            Set<String> duplicateColumns = columnUpdateForms.getDuplicateColumns();
            for (String column: duplicateColumns) {
                columnUpdateForms.deleteForm(new ColumnUpdateForm(column, null));
                String message = "엔터티의 칼럼 {0}에서 {1} 조건이 위배되었습니다.";
                message = MessageConverter.convertMessage(message, new Object[]{column, "uniqueness"});
                bindingResult.get(ENTITY_ERROR_CODE+".template.spreadSheetTable",new ArrayList<>()).get().add(message);
            }
        }

        for(ColumnUpdateForm columnUpdateForm: columnUpdateFormList){
            columnName = columnUpdateForm.getName();
            expectedType = columnUpdateForm.getType();
            List<Object> columnValues = spreadSheetTable.getColumn(columnName, false);

            if(converter.getType(columnValues) != ColumnType.valueOf(expectedType)){
                String message = "칼럼 {0}의 타입을 {1}으로 설정할 수 없습니다. 스프레드시트의 셀 수정 범위  {2}를 확인해 주세요.";
                message = MessageConverter.convertMessage(message, new Object[]{columnName, expectedType, spreadSheetTable.getColumnRange(columnName)});
                bindingResult.get( TYPE_ERROR_CODE+".template.column",new ArrayList<>()).get().add(message);
            }

            List<String> indices;
            String finalColumnName = columnName;

            List<KeyCondition> keyConditions = columnUpdateForm.getKeyConditions();
            for(KeyCondition keyCondition: keyConditions){
                KeyConditionType conditionType = keyCondition.getConditionType();
                switch(conditionType){
                    case PRIMARY_KEY, UNIQUE -> {
                        if(spreadSheetTable.hasDuplicateColumn(columnName)){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위  {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, spreadSheetTable.getColumnRange(columnName)});
                            bindingResult.get(CONDITION_ERROR_CODE + ".PRIMARY_KEY",new ArrayList<>()).get().add(message);
                        }
                    }
                    case NOT_NULL -> {
                        indices = columnValues.stream().map(val-> (String)val)
                                .filter(val -> StringUtils.isEmpty(val))
                                .map(val -> spreadSheetTable.getCellRange(finalColumnName, val))
                                .distinct().collect(Collectors.toList());
                        if(!indices.isEmpty()){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, spreadSheetTable.getColumnRange(columnName)});
                            bindingResult.get(CONDITION_ERROR_CODE + ".PRIMARY_KEY",new ArrayList<>()).get().add(message);
                        }
                    }
                }
            }

            List<ValueCondition> valueConditions = columnUpdateForm.getValConditions();

            for(ValueCondition valueCondition: valueConditions){
                ValueConditionType conditionType = valueCondition.getConditionType();
                ValueConditionTerm conditionTerm = valueCondition.getConditionTerm();
                String argument = conditionTerm.getArgument();

                switch(conditionType){
                    case EQUALS -> {
                        indices = columnValues.stream().map(val -> (String)val)
                                .filter(val -> StringUtils.isNumeric(val) && !val.equals(argument))
                                .map(val -> spreadSheetTable.getCellRange(finalColumnName, val))
                                .distinct().collect(Collectors.toList());
                        if(!indices.isEmpty()){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, indices});
                            bindingResult.get(CONDITION_ERROR_CODE + "." + conditionType,new ArrayList<>()).get().add(message);

                        }
                    }
                    case GREATER_THAN -> {
                        indices = columnValues.stream().map(val -> (String)val)
                                .filter(val -> StringUtils.isNumeric(val) && Integer.parseInt(val) <= Integer.parseInt(argument))
                                .collect(Collectors.toList());
                        if(!indices.isEmpty()){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, indices});
                            bindingResult.get(CONDITION_ERROR_CODE + "." + conditionType,new ArrayList<>()).get().add(message);
                        }
                    }
                    case LESS_THAN -> {
                        indices = columnValues.stream().map(val -> (String)val)
                                .filter(val -> StringUtils.isNumeric(val) && Integer.parseInt(val) >= Integer.parseInt(argument))
                                .distinct().collect(Collectors.toList());
                        if(!indices.isEmpty()){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, indices});
                            bindingResult.get(CONDITION_ERROR_CODE + "." + conditionType,new ArrayList<>()).get().add(message);
                        }
                    }
                    case LIKE -> {
                        indices = columnValues.stream().map(val -> (String)val)
                                .filter(value -> !QueryUtils.sqlPatternMatch(value, NamingUtils.parseString(argument)))
                                .map(val -> spreadSheetTable.getCellRange(finalColumnName, val))
                                .distinct().collect(Collectors.toList());
                        if(!indices.isEmpty()){
                            String message = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
                            message = MessageConverter.convertMessage(message, new Object[]{columnName, conditionType, indices});
                            bindingResult.get(CONDITION_ERROR_CODE + "." + conditionType,new ArrayList<>()).get().add(message);
                        }
                    }
                }
            }
        }
        log.info("bindingResult={}", bindingResult);
    }


}
