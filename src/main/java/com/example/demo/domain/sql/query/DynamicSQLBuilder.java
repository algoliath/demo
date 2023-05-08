package com.example.demo.domain.sql.query;

import com.example.demo.domain.template.model.Entity;

public interface DynamicSQLBuilder {

    String createTableQuery(Entity template);

    String deleteTableQuery(Entity template);

}
