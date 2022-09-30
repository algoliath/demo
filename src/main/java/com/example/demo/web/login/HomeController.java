package com.example.demo.web.login;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.template.TemplateStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import static com.example.demo.web.session.SessionConst.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TemplateStore templateStore;

    @GetMapping("/")
    public String homeLoginV4(@SessionAttribute(name=LOGIN_MEMBER, required = false) Member loginMember, Model model){
        // 세션에 회원 데이터가 없으면 home
        log.info("loginMember={}", loginMember);
        if(loginMember == null){
            return "login/home";
        }
        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        model.addAttribute("templates", templateStore.findByMemberId(loginMember.getId()));
        return "login/loginHome";
    }



}