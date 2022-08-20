package com.example.demo.domain.repository.template.mybatis;

import com.example.demo.domain.template.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TemplateMapper {

    void save(Template template);

}
