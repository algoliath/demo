package com.example.demo.domain.repository.template.mapper;

import com.example.demo.domain.data.dto.EntityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EntityMapper {

    void save(EntityDTO template);

    void delete(@Param("id") Long id);

    void update(EntityDTO template, @Param("id") Long id);

    void create(@Param("query") String query);

    void drop(@Param("query") String query);

    List<EntityDTO> findByMemberId(@Param("memberId") Long memberId);

    List<EntityDTO> findByName(@Param("name") String name);

    Optional<EntityDTO> findById(@Param("id") Long id);

    List<EntityDTO> findAll();
}
