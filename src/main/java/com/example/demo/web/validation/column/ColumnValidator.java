package com.example.demo.web.validation.column;

import com.example.demo.domain.column.ColumnType;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.ColumnTypeClassifier;
import com.example.demo.domain.column.form.ColumnSaveForm;
import com.example.demo.domain.template.EntityTemplateForm;
import com.example.demo.util.validation.ColumnUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ColumnValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Column.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ColumnSaveForm columnForm = (ColumnSaveForm) target;

        Set<String> validTypes = Stream.of(ColumnType.values())
                                .map(columnType -> columnType.name())
                                .collect(Collectors.toSet());

        String name = columnForm.getName();
        String type = columnForm.getType();

        // 칼럼명 에러
        if(name == null || !StringUtils.hasText(name)){
            errors.rejectValue("name", "required");
        }

        if(!ColumnUtils.isValid(name)){
            errors.rejectValue("name", "invalid");
        }

    }
}
