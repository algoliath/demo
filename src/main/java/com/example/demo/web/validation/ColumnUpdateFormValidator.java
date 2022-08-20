package com.example.demo.web.validation;

import com.example.demo.domain.form.ColumnUpdateForm;
import com.example.demo.domain.template.Column;
import com.example.demo.util.string.ColumnNameUtils;
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
public class ColumnUpdateFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Column.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ColumnUpdateForm saveForm = (ColumnUpdateForm) target;
        Set<String> validTypes = Stream.of(ColumnTypes.values()).map(columnTypes -> columnTypes.name()).collect(Collectors.toSet());

        String name = saveForm.getName();
        String type = saveForm.getType();

        log.info("[enumSet]={}", validTypes);
        log.info("[type]={}", type.toUpperCase(Locale.ROOT));

        // 칼럼명 에러
        if(name == null || !StringUtils.hasText(name)){
            errors.rejectValue("name", "required");
        }

        if(!ColumnNameUtils.isValid(name)){
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
