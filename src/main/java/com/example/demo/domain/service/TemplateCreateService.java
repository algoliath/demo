package com.example.demo.domain.service;

import com.example.demo.domain.repository.template.ColumnRepository;
import com.example.demo.domain.repository.template.mybatis.MyBatisColumnRepository;
import com.example.demo.domain.template.Template;
import com.example.demo.domain.repository.template.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateCreateService implements TemplateService {

    private final TemplateRepository templateRepository;

    @Override
    public void createTemplate(Template template) {
        log.info("template={}", template);
        templateRepository.save(template);
    }


}
