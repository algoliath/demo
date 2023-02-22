package com.example.demo.domain.data.vo;

import lombok.Data;

@Data
public class EntityForm {

    private String id;
    private String name;
    private String url;

    public EntityForm(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

}
