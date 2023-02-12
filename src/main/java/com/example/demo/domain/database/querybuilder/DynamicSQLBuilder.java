package com.example.demo.domain.database.querybuilder;

import com.example.demo.domain.template.model.Entity;

public interface DynamicSQLBuilder {

    String createTableQuery(Entity template);

    String deleteTableQuery(Entity template);

}
