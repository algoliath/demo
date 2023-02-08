package com.example.demo.domain.validation;

import com.example.demo.domain.member.Member;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class MemberValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Member.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

}
