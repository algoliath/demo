package com.example.demo.domain.data.dto;

import lombok.Data;

@Data
public class TemplateDTO {

    private Long id;
    private String name;
    private Long memberId;

    public TemplateDTO(){

    }

    public TemplateDTO(Long id, String name, Long memberId) {
        this.id = id;
        this.name = name;
        this.memberId = memberId;
    }

}
