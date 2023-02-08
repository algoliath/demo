package com.example.demo.domain.repository.template.model;

import com.example.demo.domain.data.dto.EntityDTO;

import java.util.List;
import java.util.Optional;

public interface EntityRepository {

    EntityDTO save(EntityDTO template);

    void delete(Long templateId);

    void update(EntityDTO template, Long id);

    void createEntity(String query);

    void dropEntity(String name);

    List<EntityDTO> findByMemberId(Long memberId);

    List<EntityDTO> findByName(String name);

    List<EntityDTO> findAll();
    Optional<EntityDTO> findById(Long id);


}
