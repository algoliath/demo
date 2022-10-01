package com.example.demo.domain.repository.template.model;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.template.Entity;
import com.example.demo.domain.template.Table;
import java.util.List;

public interface EntityRepository {

    void save(Entity entity);

    void update(Long id, ColumnUpdateForm updateParam);

    List<Table> findByMemberId(Long memberId);

    Table findById(Long id);

}
