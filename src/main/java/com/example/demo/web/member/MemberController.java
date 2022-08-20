package com.example.demo.web.member;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/add")
    public String addMemberForm(@ModelAttribute("member") Member member, Model model){
        model.addAttribute("member", member);
        return "members/add";
    }

    @PostMapping("/add")
    public String addMember(@Validated @ModelAttribute("member") Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "members/add";
        }
        log.info("member={}",member);
        memberRepository.save(member);
        return "redirect:/";
    }

}
