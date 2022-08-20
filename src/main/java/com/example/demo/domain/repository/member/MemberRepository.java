package com.example.demo.domain.repository.member;


import com.example.demo.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemberRepository {

    private Map<Long, Member> store = new ConcurrentHashMap<>();
    Long sequence = 0L;

    public void save(Member member){
        member.setId(sequence);
        store.put(sequence++, member);
    }

    public Member findById(Long id){
        return store.get(id);
    }

    public Optional<Member> findByLoginId(String loginId){
        return store.values().
                stream().filter(member -> member.getLoginId().equals(loginId)).
                findFirst();
    }

    public List<Member> findAll(){
        return new ArrayList<>(store.values());
    }


}
