package com.example.demo.domain.repository.document.mapper;

import com.example.demo.domain.data.dto.DocumentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DocumentMapper {

    void save(DocumentDTO Document);
    void saveBatch(List<DocumentDTO> Documents);
    void updateName(@Param("id") Long id, @Param("name") String name);
    void updateType(@Param("id") Long id, @Param("type") String type);

    void delete(@Param("id") Long id);

    List<DocumentDTO> findByTemplateId(Long templateId);

    Optional<DocumentDTO> findById(@Param("id") Long id);

}
