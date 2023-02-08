package com.example.demo.web.template;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.repository.template.model.EntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Repository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@Slf4j
@AutoConfigureMockMvc()
@SpringBootTest
class EntityControllerTest {

    @Autowired
    private MemberRepository repository;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addTemplate() {
    }

    @Test
    void syncTemplate() {
    }

    @Test
    void saveTemplate() {
    }

    @Test
    void deleteTemplate() {
    }

    @Test
    void searchTemplateFile() {
        try {
            Member member = repository.findByLoginId("test").get();
            MvcResult mvcResult = mvc.perform(post(String.format("/template/entity/%s", member.getId())).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .param("name", "Consultant"))
                    .andReturn();
            log.info("result={}", mvcResult);

            MockHttpServletResponse response = mvcResult.getResponse();
            log.info("response={}", response.getContentAsString());
        } catch (Exception e) {
            log.info("e={}", e);
        }
    }

    @Test
    void searchTemplate() {
    }

    @Test
    void template() {
    }

    @Test
    void templates() {
    }

    @Test
    void editColumn() {
    }
}