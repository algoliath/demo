package com.example.demo;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInit {

    private final MemberStore memberRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("test!");
        member.setName("테스터");

        // bad practice
        Path path = Paths.get("src/main/resources/auth/credentials.json");
        String name = "credentials.json";
        String originalFileName = "credentials.json";
        String contentType = "application/json";
        MultipartFile multipartFile = null;
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
            multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);
        } catch (final IOException e) {
        }

        log.info("multipartFile={}", multipartFile);
        member.setAttachFile(multipartFile);
        memberRepository.save(member);
    }

}