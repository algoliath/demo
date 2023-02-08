package com.example.demo.domain;

import com.example.demo.DemoApplication;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.template.type.TemplateType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
@SpringBootTest
public class BeanValidationTest {

    @Autowired
    private TemplateService templateService;

    @Test
    void findBeansWithTargetMethod(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(DemoApplication.class);
        for(String beanName: ac.getBeanDefinitionNames()){
            log.info("bean={}", beanName);
        }
    }

    @Test
    void findEntityBeansWithTargetMethod(){
        Entity target = (Entity) templateService.findTemplatesByName("template1").get(0);
        Entity entity = (Entity) templateService.findTemplatesByName("template1").get(0);
        Assertions.assertThat(entity.equals(target)).isEqualTo(true);
    }


}
