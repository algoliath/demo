package com.example.demo.domain.repository.column.model;

import com.example.demo.domain.data.dto.ColumnDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository {


    ColumnDTO save(ColumnDTO column);

    void saveBatch(List<ColumnDTO> columns);

    void delete(Long templateId);

    void updateName(@Param("id") Long id, @Param("target") String name);

    void updateType(@Param("id") Long id, @Param("target") String type);

    List<com.example.demo.domain.data.dto.ColumnDTO> findByTemplateId(@Param("template_id") Long templateId);

    Optional<com.example.demo.domain.data.dto.ColumnDTO> findById(@Param("id") Long id);

}
