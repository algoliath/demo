package com.example.demo.domain.template.service;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.data.dto.ColumnDTO;
import com.example.demo.domain.data.dto.ConditionDTO;
import com.example.demo.domain.data.dto.TemplateDTO;
import com.example.demo.domain.database.mapper.EntityDynamicSQLMapper;
import com.example.demo.domain.repository.condition.mapper.KeyConditionMapper;
import com.example.demo.domain.repository.condition.mapper.ValueConditionMapper;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.repository.column.model.ColumnRepository;
import com.example.demo.domain.repository.template.mapper.TemplateMapper;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityTemplateService implements TemplateService {

    private final EntityRepository entityRepository;
    private final ColumnRepository columnRepository;
    private final TemplateMapper templateMapper;
    private final KeyConditionMapper keyConditionMapper;
    private final ValueConditionMapper valConditionMapper;
    private final EntityDynamicSQLMapper sqlMapper;

    @Transactional
    public List<Template> getTemplates(Long memberId){
        List<EntityDTO> entityDTOList = entityRepository.findByMemberId(memberId);
        List<Template> templates = new ArrayList<>();
        for(EntityDTO entityDTO : entityDTOList){
            Entity entity = new Entity(entityDTO);
            for(Column column: findColumnsByTemplateId(entityDTO.getId())){
                entity.addColumn(column);
            }
            templates.add(entity);
        }
        return templates;
    }

    @Transactional
    public List<Column> findColumnsByTemplateId(Long templateId){
        List<com.example.demo.domain.data.dto.ColumnDTO> columnDTOS = columnRepository.findByTemplateId(templateId);
        List<Column> columns = new ArrayList<>();
        for(com.example.demo.domain.data.dto.ColumnDTO column: columnDTOS){
            List<ConditionDTO> keyCondition = keyConditionMapper.findByColumnId(column.getId());
            List<ConditionDTO> valCondition = valConditionMapper.findByColumnId(column.getId());
            columns.add(new Column(column, keyCondition, valCondition));
        }
        return columns;
    }

    @Transactional
    public List<Template> findTemplatesByName(String templateName) {
        List<Template> templates = new ArrayList<>();
        if(StringUtils.hasText(templateName)){
            List<EntityDTO> entities = entityRepository.findByName("%" + templateName + "%");
            for(EntityDTO entityDTO : entities){
                Entity entity = new Entity(entityDTO);
                templates.add(entity);
            }
        }
        return templates;
    }

    @Transactional
    public Optional<Template> findTemplateById(Long templateId) {
        Optional<EntityDTO> dto = entityRepository.findById(templateId);
        if(dto.isEmpty()){
            throw new IllegalStateException("템플릿 아이디에" + templateId + "해당하는 엔티티가 존재하지 않습니다");
        }
        return Optional.of(new Entity(dto.get()));
    }

    @Transactional
    public void saveCondition(Column column){
        for(KeyCondition keyCondition: column.getKeyConditions()){
            keyConditionMapper.save(new ConditionDTO(keyCondition.getConditionType().toString(), null), column.getId());
        }
        for(ValueCondition valueCondition: column.getValueConditions()){
            valConditionMapper.save(new ConditionDTO(valueCondition.getConditionType().toString(), valueCondition.getConditionTerm().getArgument().toString()), column.getId());
        }
    }

    @Transactional
    public List<Column> saveColumns(List<Column> columns, Long templateId){
        for(Column column: columns){
            ColumnDTO columnDTO = columnRepository.save(new ColumnDTO(column, templateId));
            column.setId(columnDTO.getId());
            saveCondition(column);
        }
        return columns;
    }

    @Transactional
    public EntityDTO saveTemplate(Entity entity, Long memberId){
        EntityDTO entitySaveDTO = new EntityDTO(entity, memberId);
        registerTemplate(entitySaveDTO);
        EntityDTO entityDTO = registerEntity(entitySaveDTO);
        saveColumns(entity.getColumns(), entityDTO.getId());
        createTable(sqlMapper.createTableQuery(entity));
        insertColumns(entity);
        return entityDTO;
    }

    @Transactional
    public void createTable(String sqlMapper) {
        entityRepository.createEntity(sqlMapper);
    }

    @Transactional
    public EntityDTO registerEntity(EntityDTO entityDTO){
        return entityRepository.save(entityDTO);
    }

    @Transactional
    public void registerTemplate(TemplateDTO templateDTO) {
        templateMapper.save(templateDTO);
    }
    @Transactional
    public void insertColumns(Entity entity){
        createTable(sqlMapper.insertTableQuery(entity));
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        Optional<EntityDTO> entityDAO = entityRepository.findById(templateId);
        if(templateId == null){
            log.error("templateId={}", templateId);
            throw new IllegalArgumentException("잘못된 템플릿 아이디가 들어왔습니다");
        }
        if(entityDAO.isEmpty()){
            throw new IllegalStateException("존재하지 않는 템플릿입니다");
        }
        Entity entity = new Entity(entityDAO.get());
        columnRepository.delete(templateId);
        entityRepository.delete(templateId);
        entityRepository.dropEntity(sqlMapper.deleteTableQuery(entity));
    }

}
