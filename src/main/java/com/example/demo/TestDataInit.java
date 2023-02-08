package com.example.demo;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MemberRepository memberRepository;


    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {

        Member member = memberRepository.findByLoginId("test").orElse(null);

        if(member != null){
            return;
        }

        member = new Member();
        member.setLoginId("test");
        member.setLoginPw("test!");
        member.setName("테스터");

        // 파일이나 환경 변수에서 가져오는 방향으로 수정 필요
        Path path = Paths.get("src/main/resources/auth/credentials.json");
        String name = "credentials.json";
        String originalFileName = "credentials.json";
        String contentType = "application/json";
        MultipartFile multipartFile = null;

        byte[] content;
        try {
            content = Files.readAllBytes(path);
            multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);
        } catch (final IOException e) {

        }

        log.info("multipartFile={}", multipartFile);
        member.setAttachFile(multipartFile);
        member.setFileId(multipartFile.getOriginalFilename());

        memberRepository.save(member);
        log.info("generated member={}", memberRepository.findByLoginId(member.getLoginId()));
    }

}