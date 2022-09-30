package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.repository.template.mapper.TemplateMapper;
import com.example.demo.domain.template.Table;
import com.example.demo.domain.repository.template.model.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class MyBatisTemplateRepository implements TemplateRepository {
//
//    private final TemplateMapper templateMapper;
//
//    @Override
//    public void save(Table template) {
//        templateMapper.save(template);
//    }
//
//    @Override
//    public void update(Long id, ColumnUpdateForm updateParam) {
//        templateMapper.update(id, updateParam);
//    }
//
//    @Override
//    public List<Table> findByMemberId(Long memberId){
//        log.info("[List<Template>]:", templateMapper.findAll(memberId));
//        return null;
//    }
//
//    @Override
//    public Table findById(Long id){
//        log.info("[Template]:", templateMapper.findById(id));
//        return null;
//    }
//
//}
