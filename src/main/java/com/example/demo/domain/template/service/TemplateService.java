package com.example.demo.domain.template.service;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.exception.ForeignKeyException;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Query;
import com.example.demo.domain.template.model.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    EntityDTO saveEntity(Entity entity, Long memberId);

    List<Column> saveColumns(List<Column> columns, Long templateId);

    void saveCondition(Column column);

    void deleteTemplate(Long templateId) throws ForeignKeyException;

    List<Column> findColumnsByTemplateId(Long templateId);

    List<Template> getTemplates(Long memberId);

    Optional<Template> findTemplateById(Long templateId);

    // this method should be removed
    List<Entity> findEntitiesByName(String templateName);

    // this method should be removed
    List<Query> findQueriesByName(String templateName);

    List<Template> findTemplatesByNameAndMemberId(String templateName, Long memberId);
}
