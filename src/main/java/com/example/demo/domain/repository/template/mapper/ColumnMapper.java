package com.example.demo.domain.repository.template.mapper;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import java.util.List;

public interface ColumnMapper {

    void save(Column column);

    void batchSave(List<Column> column);

    void update(Long id, ColumnUpdateForm updateParam);

    List<Column> findAll(Long templateId);

    Column findById(Long id);
}
