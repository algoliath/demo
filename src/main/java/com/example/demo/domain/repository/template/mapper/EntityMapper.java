package com.example.demo.domain.repository.template.mapper;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.template.Entity;
import com.example.demo.domain.template.Table;

import java.util.List;


public interface EntityMapper {

    void save(Entity entity);

    void update(Long id, ColumnUpdateForm updateParam);

    List<Table> findAll(Long memberId);

    Table findById(Long id);

}
