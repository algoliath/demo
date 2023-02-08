package com.example.demo.domain.repository.condition.mapper;

import com.example.demo.domain.data.dto.ConditionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ValueConditionMapper {

    void save(ConditionDTO condition, Long columnId);

    void saveBatch(List<ConditionDTO> conditions);

    void delete(@Param("id") String id);

    void updateType(@Param("id") String id, @Param("type") String name);

    void updateTerm(@Param("id") String id, @Param("term") String type);

    List<com.example.demo.domain.data.dto.ConditionDTO> findAll();

    List<com.example.demo.domain.data.dto.ConditionDTO> findByColumnId(Long columnId);

    Optional<com.example.demo.domain.data.dto.ConditionDTO> findById(@Param("id") String id);

}
