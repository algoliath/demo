package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.repository.template.mapper.ColumnMapper;
import com.example.demo.domain.repository.template.model.ColumnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class MyBatisColumnRepository implements ColumnRepository {
//
//    private final ColumnMapper columnMapper;
//
//    @Override
//    public void save(Column column) {
//        columnMapper.save(column);
//    }
//
//    @Override
//    public void batchSave(List<Column> columns){
//        columnMapper.batchSave(columns);
//    }
//
//    @Override
//    public void update(Long id, ColumnUpdateForm updateParam) {
//        columnMapper.update(id, updateParam);
//    }
//
//    @Override
//    public Column findById(Long id){
//        log.info("[Column]:", columnMapper.findById(id));
//        return null;
//    }
//    @Override
//    public List<Column> findByTemplateId(Long templateId){
//        log.info("[List<Column>]:", columnMapper.findAll(templateId));
//        return null;
//    }
//
//}
