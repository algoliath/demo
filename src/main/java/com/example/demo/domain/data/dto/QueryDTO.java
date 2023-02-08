package com.example.demo.domain.data.dto;

import com.example.demo.domain.template.model.Entity;
import lombok.Data;

@Data
public class QueryDTO {

    private Long id;
    private String name;
    private Long memberId;
    private String fileName;
    private String fileDomain;

    public QueryDTO(Long id, String name, String fileName, String fileDomain, Long memberId) {
        this.id = id;
        this.name = name;
        this.memberId = memberId;
        this.fileName = fileName;
        this.fileDomain = fileDomain;
    }

    public QueryDTO(Entity template, Long memberId) {
        this.name = template.getName();
        this.fileName = template.getSheetTitle();
        this.fileDomain = template.getSheetRange();
        this.memberId = memberId;
    }

}
