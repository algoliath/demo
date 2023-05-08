package com.example.demo.domain.template.service;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.data.vo.template.QueryColumnUpdateForm;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.data.dto.ColumnDTO;
import com.example.demo.domain.data.dto.ConditionDTO;
import com.example.demo.domain.data.dto.TemplateDTO;
import com.example.demo.domain.sql.query.EntityDynamicSQLBuilder;
import com.example.demo.domain.exception.ForeignKeyException;
import com.example.demo.domain.repository.condition.mapper.KeyConditionMapper;
import com.example.demo.domain.repository.condition.mapper.ValueConditionMapper;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.repository.column.model.ColumnRepository;
import com.example.demo.domain.repository.template.mapper.TemplateMapper;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Query;
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
    private final EntityDynamicSQLBuilder sqlMapper;

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
    public List<Entity> findEntitiesByName(String templateName) {
        List<Entity> templates = new ArrayList<>();
        if(StringUtils.hasText(templateName)){
            List<EntityDTO> entities = entityRepository.findByName(templateName);
            for(EntityDTO entityDTO : entities){
                Entity entity = new Entity(entityDTO);
                for (Column column : findColumnsByTemplateId(entity.getId())) {
                     entity.addColumn(column);
                }
                templates.add(entity);
            }
        }
        return templates;
    }

    @Transactional
    public Entity findEntityById(Long templateId) {
        Entity entity = new Entity(entityRepository.findById(templateId).orElseThrow(() -> new IllegalArgumentException()));
        for (Column column : findColumnsByTemplateId(entity.getId())) {
            entity.addColumn(column);
        }
        return entity;
    }


    @Transactional
    public List<Query> findQueriesByName(String templateName) {
        List<Query> templates = new ArrayList<>();
        if(StringUtils.hasText(templateName)){
            List<EntityDTO> entities = entityRepository.findByName(templateName);
            for(EntityDTO entityDTO : entities){
                Query query = new Query(entityDTO);
                List<Column> columns = findColumnsByTemplateId(query.getId());
                columns.stream().map(column -> new QueryColumnUpdateForm(column.getColumnName().getValidName()))
                        .iterator()
                        .forEachRemaining((column) -> query.addColumn(column));
                templates.add(query);
            }
        }
        return templates;
    }

    @Transactional
    public List<Template> findTemplatesByNameAndMemberId(String templateName, Long memberId) {
        List<Template> templates = new ArrayList<>();
        if(StringUtils.hasText(templateName)){
            List<EntityDTO> entities = entityRepository.searchByCond(templateName, memberId);
            for(EntityDTO entityDTO : entities){
                Entity entity = new Entity(entityDTO);
                for (Column column : findColumnsByTemplateId(entity.getId())) {
                    entity.addColumn(column);
                }
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
    public EntityDTO saveEntity(Entity entity, Long memberId){
        // CREATE NEW ENTITY TABLE (DDL)
        createTable(sqlMapper.createTableQuery(entity));

        EntityDTO entitySaveDTO = new EntityDTO(entity, memberId);

        // INSERT INTO TEMPLATE/ENTITY/COLUMN TABLE(DML)
        saveTemplate(entitySaveDTO);
        EntityDTO entityDTO = registerEntity(entitySaveDTO);
        saveColumns(entity.getColumns(), entityDTO.getId());

        // INSERT ROWS INTO CREATED ENTITY TABLE(DML)
        insertColumns(entity);
        return entityDTO;
    }

    @Transactional
    public void createTable(String sqlMapper) {
        entityRepository.createEntity(sqlMapper);
    }

    @Transactional
    public void saveTemplate(TemplateDTO templateDTO) {
        templateMapper.save(templateDTO);
    }
    @Transactional
    public void insertColumns(Entity entity){
        createTable(sqlMapper.insertTableQuery(entity));
    }

    @Transactional
    public EntityDTO registerEntity(EntityDTO entityDTO){
        return entityRepository.save(entityDTO);
    }

    @Transactional
    public void deleteTemplate(Long templateId) throws ForeignKeyException {
        if(templateId == null){
            log.error("templateId={}", templateId);
            throw new IllegalArgumentException("잘못된 템플릿 아이디가 들어왔습니다");
        }
        EntityDTO entityDAO = entityRepository.findById(templateId).orElseThrow(IllegalStateException::new);
        Entity entity = new Entity(entityDAO);
        columnRepository.delete(templateId);
        templateMapper.delete(templateId);
        entityRepository.dropEntity(sqlMapper.deleteTableQuery(entity));
    }

}
