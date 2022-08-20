package com.example.demo.domain.repository.template;

import com.example.demo.domain.form.ColumnUpdateForm;
import com.example.demo.domain.template.Template;

public interface TemplateRepository {

    void save(Template template);

    void update(Long id, ColumnUpdateForm updateParam);

}
