package com.example.demo.domain.template;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.ColumnName;

import java.util.*;

public class Table extends Template{

    public Table(String name, Map<String, Column> columns, String memberId) {
        super(name, TemplateType.TABLE, columns, memberId);
        setKeyColumn(new Column("docs_id", "character"));
    }

    @Override
    public Object convert() {
        return null;
    }

    @Override
    public boolean supports() {
        return false;
    }

}
