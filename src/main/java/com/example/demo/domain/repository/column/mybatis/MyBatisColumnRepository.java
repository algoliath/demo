package com.example.demo.domain.repository.column.mybatis;

import com.example.demo.domain.data.dto.ColumnDTO;
import com.example.demo.domain.repository.column.mapper.ColumnMapper;
import com.example.demo.domain.repository.column.model.ColumnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisColumnRepository implements ColumnRepository {

    private final ColumnMapper columnMapper;

    @Override
    public ColumnDTO save(ColumnDTO column) {
        columnMapper.save(column);
        return column;
    }

    @Override
    public void saveBatch(List<ColumnDTO> columns){
        columnMapper.saveBatch(columns);
    }

    @Override
    public void delete(Long templateId) {
        columnMapper.delete(templateId);
    }

    @Override
    public void updateName(Long id, String name) {
        columnMapper.updateName(id, name);
    }

    @Override
    public void updateType(Long id, String type) {
        columnMapper.updateType(id, type);
    }

    @Override
    public Optional<com.example.demo.domain.data.dto.ColumnDTO> findById(Long id){
        log.info("[List<Column>]:", columnMapper.findById(id));
        return columnMapper.findById(id);
    }
    @Override
    public List<com.example.demo.domain.data.dto.ColumnDTO> findByTemplateId(Long templateId){
        log.info("[List<Column>]:", columnMapper.findByTemplateId(templateId));
        return columnMapper.findByTemplateId(templateId);
    }

}
