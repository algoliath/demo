package com.example.demo.domain.data.dto;
import lombok.Data;

@Data
public class ConditionDTO {

    private String id;
    private String type;
    private String term;

    public ConditionDTO(){

    }

    public ConditionDTO(String type, String term) {
        this.type = type;
        this.term = term;
    }

    public ConditionDTO(String id, String type, String term) {
        this.id = id;
        this.type = type;
        this.term = term;
    }
}
