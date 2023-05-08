package com.example.demo.web.controller;

import com.example.demo.domain.data.vo.template.entity.EntitySearchForm;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.login.LoginForm;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.form.MemberForm;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final EntityRepository entityRepository;

    @GetMapping("/")
    public String homeLoginV4(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                              @ModelAttribute LoginForm form,
                              @ModelAttribute("member") MemberForm memberForm, Model model){
        // 세션에 회원 데이터가 없으면 home
        log.info("loginMember={}", loginMember);
        if(loginMember == null){
            return "login/home";
        }
        Member member = memberRepository.findByLoginId(loginMember.getLoginId()).get();
        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("memberId", member.getRandomUUID());
        try{
            List<EntityDTO> entityDTOList = entityRepository.findByMemberId(member.getId());
            List<Entity> entityList = entityDTOList.stream().map(entityDto -> new Entity(entityDto)).collect(Collectors.toList());
            model.addAttribute("templates", entityList);
        } catch(Exception e){
            log.info("Exception occurred during transaction. Caused by={}", e);
        }
        model.addAttribute("entitySearchForm", new EntitySearchForm());
        return "login/loginHome";
    }
}