package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.repository.template.mapper.EntityMapper;
import com.example.demo.domain.repository.template.model.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisEntityRepository implements EntityRepository {

    private final EntityMapper entityMapper;

    @Override
    public EntityDTO save(EntityDTO template) {
        entityMapper.save(template);
        return template;
    }

    @Override
    public void delete(Long id) {
        entityMapper.delete(id);
    }

    @Override
    public void update(EntityDTO template, Long id) {
        entityMapper.update(template, id);
    }

    @Override
    public void createEntity(@Param("value") String createQuery) {
           entityMapper.create(createQuery);
    }

    @Override
    public void dropEntity(String query) {
           entityMapper.drop(query);
    }

    @Override
    public List<EntityDTO> findByMemberId(Long memberId){
        List<EntityDTO> entities = entityMapper.findByMemberId(memberId);
        log.info("[List<Template>]:{}", entities);
        return entities;
    }

    @Override
    public List<EntityDTO> findByName(String name) {
        List<EntityDTO> entities = entityMapper.findByName(name);
        log.info("[List<Template>]:{}", entities);
        return entities;
    }

    @Override
    public List<EntityDTO> searchByCond(String name, Long memberId) {
        log.info("[List<Template>]:{}", entityMapper.searchByCond(name, memberId));
        return entityMapper.searchByCond(name, memberId);
    }

    @Override
    public List<EntityDTO> findAll() {
        return entityMapper.findAll();
    }

    @Override
    public Optional<EntityDTO> findById(Long id){
        Optional<EntityDTO> optionalEntity = entityMapper.findById(id);
        log.info("[Template]:{}", optionalEntity);
        return optionalEntity;
    }

}
