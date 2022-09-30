package com.example.demo.domain.login;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberStore memberRepository;

    public Member login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .orElse(null);
    }

}
