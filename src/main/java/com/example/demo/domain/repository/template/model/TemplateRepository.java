package com.example.demo.domain.repository.template.model;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.template.Table;

import java.util.List;

public interface TemplateRepository {

    void save(Table template);

    void update(Long id, ColumnUpdateForm updateParam);

    List<Table> findByMemberId(Long memberId);

    Table findById(Long id);


}
