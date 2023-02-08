package com.example.demo.domain.data.dto;

import com.example.demo.domain.column.Column;
import lombok.Data;

@Data
public class ColumnDTO {

    private Long id;
    private String name;
    private String type;
    private Long templateId;

    public ColumnDTO(Column column, Long templateId) {
        this.name = column.getColumnName().getValidName();
        this.type = column.getType();
        this.templateId = templateId;
    }

    public ColumnDTO(Long id, Long templateId, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.templateId = templateId;
    }

}
