package com.example.demo.domain.database.mapper;

import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;

public interface DynamicSQLMapper {

    String createTableQuery(Entity template);

    String deleteTableQuery(Entity template);

}
