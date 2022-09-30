package com.example.demo.domain.repository.member;


import com.example.demo.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MemberStore {

    private Map<String, Member> store = new ConcurrentHashMap<>();

    public void save(Member member){
        store.put(member.getId(), member);
    }

    public Member findById(String id){
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
