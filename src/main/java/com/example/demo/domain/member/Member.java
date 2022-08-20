package com.example.demo.domain.member;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Member {

    public Member(){

    }

    Long id;

    @NotNull
    String name;

    @NotNull
    String loginId;

    @NotNull
    String password;

}
