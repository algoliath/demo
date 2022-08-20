package com.example.demo.domain.template;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data
public class Template {

    private Long memberId;
    private String name;

    public Template(Long memberId, String name) { // create defensive copy of the list
        this.memberId = memberId;
        this.name = name;
    }

}
