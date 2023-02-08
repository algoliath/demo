package com.example.demo.domain.repository.column.mapper;

import com.example.demo.domain.data.dto.ColumnDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ColumnMapper {

    void save(ColumnDTO column);
    void saveBatch(List<ColumnDTO> columns);
    void updateName(@Param("id") Long id, @Param("name") String name);
    void updateType(@Param("id") Long id, @Param("type") String type);

    void delete(@Param("id") Long id);

    List<ColumnDTO> findByTemplateId(Long templateId);

    Optional<ColumnDTO> findById(@Param("id") Long id);

}
