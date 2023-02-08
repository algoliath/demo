package com.example.demo.domain.repository.condition.mapper;

import com.example.demo.domain.data.dto.ConditionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface KeyConditionMapper {

    void save(ConditionDTO condition, Long columnId);

    void saveBatch(List<ConditionDTO> conditions);

    void delete(@Param("id") String id);

    void updateType(@Param("id") String id, @Param("type") String type);

    List<ConditionDTO> findAll();

    List<ConditionDTO> findByColumnId(Long columnId);

    Optional<ConditionDTO> findById(@Param("id") String id);

}
