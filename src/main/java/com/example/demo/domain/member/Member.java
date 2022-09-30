package com.example.demo.domain.member;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Data
public class Member {

    // repository
    private final String id;
    private String name;

    // login form
    private String loginId;
    private String password;
    private MultipartFile attachFile;

    public Member(String name, String loginId, String password, MultipartFile attachFile) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.attachFile = attachFile;
    }

    public Member(){
        this.id = UUID.randomUUID().toString();
    }

    public boolean matchId(String memberId){
        return id.equals(memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
