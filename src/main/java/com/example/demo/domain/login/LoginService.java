package com.example.demo.domain.login;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getLoginPw().equals(password))
                .orElse(null);
    }

}
