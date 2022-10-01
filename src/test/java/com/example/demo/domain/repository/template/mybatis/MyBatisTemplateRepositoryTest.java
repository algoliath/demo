package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.ColumnName;
import com.example.demo.domain.column.ColumnType;
import com.example.demo.domain.repository.template.mapper.TemplateMapper;
import com.example.demo.domain.template.Table;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
class MyBatisTemplateRepositoryTest {

    Map<String, Column> map;

    @BeforeEach
    void setup(){
        map = new HashMap<>();
        map.put("column1", new Column("column1", ColumnType.CHARACTER.getOracle()));
    }

    @Test
    void save() {
        Table template = new Table("table1", map, "member1");
    }

    @Test
    void update() {

    }

    @Test
    void findByMemberId() {

    }

    @Test
    void findById() {
    }
}