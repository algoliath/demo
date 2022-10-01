package com.example.demo.web.member;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.form.MemberForm;
import com.example.demo.domain.repository.member.MemberStore;
import com.example.demo.domain.repository.uploadFile.UploadFileRepository;
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

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberStore memberRepository;
    private final UploadFileRepository fileRepository;

    @GetMapping("/add")
    public String addMemberForm(@ModelAttribute("member") MemberForm memberForm, Model model){
        model.addAttribute("member", memberForm);
        return "members/add";
    }

    @PostMapping("/add")
    public String addMember(@Validated @ModelAttribute("member") MemberForm memberForm, BindingResult bindingResult) throws IOException {
        log.info("member={}",memberForm);
        log.info("bindingResult={}", bindingResult);
        if(bindingResult.hasErrors()){
            return "members/add";
        }
        fileRepository.storeFile(memberForm.getAttachFile());
        Member member = new Member(memberForm.getName(), memberForm.getLoginId(), memberForm.getPassword(), memberForm.getAttachFile());
        memberRepository.save(member);
        return "redirect:/";
    }

}