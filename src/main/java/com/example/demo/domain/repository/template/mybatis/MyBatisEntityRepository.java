package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.repository.template.mapper.EntityMapper;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.example.demo.domain.template.Entity;
import com.example.demo.domain.template.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class MyBatisEntityRepository implements EntityRepository {
//
//    private final EntityMapper entityMapper;
//
//    @Override
//    public void save(Entity entity) {
//        entityMapper.save(entity);
//    }
//
//    @Override
//    public void update(Long id, ColumnUpdateForm updateParam) {
//        entityMapper.update(id, updateParam);
//    }
//
//    @Override
//    public List<Table> findByMemberId(Long memberId){
//        log.info("[List<Template>]:", entityMapper.findAll(memberId));
//        return null;
//    }
//
//    @Override
//    public Table findById(Long id){
//        log.info("[Template]:", entityMapper.findById(id));
//        return null;
//    }
//
//}
