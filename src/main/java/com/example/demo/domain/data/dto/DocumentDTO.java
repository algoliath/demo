package com.example.demo.domain.data.dto;

import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import lombok.Data;

@Data
public class DocumentDTO {

    private String id;
    private MimeTypes type;
    private Long templateId;

    public DocumentDTO(String id, MimeTypes type, Long templateId) {
        this.id = id;
        this.type = type;
        this.templateId = templateId;
    }

}
