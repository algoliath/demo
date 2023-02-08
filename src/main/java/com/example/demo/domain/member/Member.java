package com.example.demo.domain.member;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Component
public class Member {

    private Long id;
    private String randomUUID = UUID.randomUUID().toString().substring(0, 20);
    private String name;
    private String fileId;
    private String loginId;
    private String loginPw;
    private MultipartFile attachFile;

    public Member(){
    }
    public Member(String name, String loginId, String loginPw, MultipartFile attachFile) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.attachFile = attachFile;
        this.fileId = attachFile.getOriginalFilename();
    }

    public Member(String name, String fileId, String loginId, String loginPw) {
        this.name = name;
        this.fileId = fileId;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

}
