package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.form.ColumnUpdateForm;
import com.example.demo.domain.template.Column;
import com.example.demo.domain.template.Template;
import com.example.demo.domain.repository.template.TemplateRepository;
import com.example.demo.web.validation.ColumnTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisTemplateRepository implements TemplateRepository {

    private final TemplateMapper templateMapper;

    @Override
    public void save(Template template) {

    }

    @Override
    public void update(Long id, ColumnUpdateForm updateParam) {

    }

}
