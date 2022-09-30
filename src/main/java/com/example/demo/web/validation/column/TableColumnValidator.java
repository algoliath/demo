package com.example.demo.web.validation.column;

import com.example.demo.domain.column.ColumnType;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.Column;
import com.example.demo.util.validation.ColumnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class TableColumnValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Column.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ColumnUpdateForm saveForm = (ColumnUpdateForm) target;
        Set<String> validTypes = Stream.of(ColumnType.values()).map(columnType -> columnType.name()).collect(Collectors.toSet());

        String name = saveForm.getName();
        String type = saveForm.getType();

        log.info("[enumSet]={}", validTypes);
        log.info("[type]={}", type.toUpperCase(Locale.ROOT));

        // 칼럼명 에러
        if(name == null || !StringUtils.hasText(name)){
            errors.rejectValue("name", "required");
        }

        if(!ColumnUtils.isValid(name)){
            errors.rejectValue("name", "invalid");
        }

        // 칼럼타입 에러
        if(type == null){
            errors.rejectValue("type", "required");
        }

        if(!StringUtils.hasText(type)) {
            errors.rejectValue("type", "required");
        }

        if(!validTypes.contains(type.toUpperCase(Locale.ROOT))){
            errors.rejectValue("type", "invalid");
        }

    }
}
