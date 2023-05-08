package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.type.TemplateType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
class MyBatisEntityRepositoryTest {

    @Autowired
    private EntityRepository repository;

    private Entity entity;

    @BeforeEach
    void setup(){
        EntityTemplateForm templateForm = new EntityTemplateForm();
        templateForm.setName("template*");
        templateForm.setType(TemplateType.ENTITY.toString());
        templateForm.setSpreadSheetTitle("Consultant");
        templateForm.setSpreadSheetRange("Sheet1!A1:D5");
        entity = new Entity(templateForm);
    }

    @Test
    @Transactional
    void save() {
        EntityDTO entityDTO = repository.save(new EntityDTO(entity, 1L));
        log.info("entitySaveDTO={}", entityDTO);
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