package com.example.demo.domain.repository.template.mapper;
import com.example.demo.domain.data.dto.TemplateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TemplateMapper {

    void save(TemplateDTO template);

    void delete(@Param("id") Long id);

    void update(TemplateDTO template, @Param("id") Long id);

    void create(@Param("query") String query);

    void drop(@Param("query") String query);

    List<TemplateDTO> findByMemberId(@Param("memberId") Long memberId);

    List<TemplateDTO> findByName(@Param("name") String name);

    Optional<TemplateDTO> findById(@Param("id") Long id);

    List<TemplateDTO> findAll();


}
