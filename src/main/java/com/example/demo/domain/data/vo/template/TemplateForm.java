package com.example.demo.domain.data.vo.template;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.template.type.TemplateType;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TemplateForm {

    @NotBlank
    private String name;
    @NotBlank
    private String type;

    public TemplateForm() {
    }

    public TemplateForm(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public boolean isValidForm() {
        return StringUtils.hasText(name) && type.equals(TemplateType.ENTITY);
    }
}