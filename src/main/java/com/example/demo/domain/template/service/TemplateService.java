package com.example.demo.domain.template.service;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    EntityDTO saveTemplate(Entity entity, Long memberId);

    List<Column> saveColumns(List<Column> columns, Long templateId);

    void saveCondition(Column column);

    void deleteTemplate(Long templateId);

    List<Column> findColumnsByTemplateId(Long templateId);

    List<Template> getTemplates(Long memberId);

    Optional<Template> findTemplateById(Long templateId);

    List<Template> findTemplatesByName(String templateName);

}
