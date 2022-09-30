package com.example.demo.domain.repository.template.model;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.form.ColumnUpdateForm;

import java.util.List;

public interface ColumnRepository {

    void save(Column column);


    void batchSave(List<Column> columns);

    void update(Long id, ColumnUpdateForm from);

    List<Column> findByTemplateId(Long templateId);

    Column findById(Long id);

}
