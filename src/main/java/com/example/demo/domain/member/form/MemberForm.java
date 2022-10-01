package com.example.demo.domain.member.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberForm {

    private String name;

    private String loginId;

    private String password;

    private MultipartFile attachFile;

}
