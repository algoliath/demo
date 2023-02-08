package com.example.demo.domain.repository.member;

import com.example.demo.domain.member.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    void updateName(Long id, String name);

    void updateLoginId(Long id, String loginId);

    void updateLoginPw(Long id, String loginPw);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findById(Long id);

    List<Member> findAll();


}
