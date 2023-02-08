package com.example.demo.domain.repository.member.mapper;

import com.example.demo.domain.member.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    void save(Member member);

    void updateName(@Param("id") Long id, @Param("target") String name);

    void updateLoginId(@Param("id") Long id, @Param("target") String loginId);

    void updateLoginPw(@Param("id") Long id, @Param("target") String loginPw);

    Optional<Member> findByLoginId(@Param("login_id") String loginId);

    Optional<Member> findById(@Param("id") Long id);

    List<Member> findAll();

}
