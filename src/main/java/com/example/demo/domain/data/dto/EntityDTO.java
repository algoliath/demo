package com.example.demo.domain.data.dto;

import com.example.demo.domain.template.model.Entity;
import lombok.Data;

@Data
public class EntityDTO extends TemplateDTO{

    private Long memberId;
    private String fileName;
    private String fileDomain;

    public EntityDTO(Long id, String name, String fileName, String fileDomain, Long memberId) {
        super(id, name, memberId);
        this.fileName = fileName;
        this.fileDomain = fileDomain;
    }

    public EntityDTO(Entity entity, Long memberId) {
        super(entity.getId(), entity.getName(), memberId);
        this.fileName = entity.getSheetTitle();
        this.fileDomain = entity.getSheetRange();
    }

    public Long getId(){
        return super.getId();
    }

    public Long getMemberId(){
        return super.getMemberId();
    }

    public String getName() {
        return super.getName();
    }

}
