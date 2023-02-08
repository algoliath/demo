package com.example.demo.domain.repository.member;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MyBatisMemberRepository implements MemberRepository {

    private final MemberMapper memberMapper;

    @Override
    public void save(Member member) {
        memberMapper.save(member);
    }

    @Override
    public void updateName(Long id, String name) {
        memberMapper.updateName(id, name);
    }

    @Override
    public void updateLoginId(Long id, String loginId) {
        memberMapper.updateLoginId(id, loginId);
    }

    @Override
    public void updateLoginPw(Long id, String loginPw) {
        memberMapper.updateLoginPw(id, loginPw);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberMapper.findById(id);
    }

    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }
}
