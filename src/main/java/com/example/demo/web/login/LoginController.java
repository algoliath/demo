package com.example.demo.web.login;

import com.example.demo.domain.login.LoginService;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.login.LoginForm;
import com.example.demo.web.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }


    @PostMapping("/login")
    public String loginFormV3(@ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                              @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request){

        log.info("request={}", request);
        log.info("requestURL={}", redirectURL);
        log.info("loginForm={}", form);

        // 1차 검증 (필드값 확인)
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 2차 검증 (식별값 확인)
        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성해서 반환
        HttpSession session = request.getSession(true); // default = true(생성)
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/logout")
    public String logoutV3(HttpServletRequest request){
        HttpSession session = request.getSession(false); // 세션이 없을 경우 null 반환
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }
}
