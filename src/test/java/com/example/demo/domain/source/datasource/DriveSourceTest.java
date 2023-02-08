package com.example.demo.domain.source.datasource;

import com.example.demo.DemoApplication;
import com.example.demo.domain.member.Member;
import com.example.demo.util.Source;
import com.example.demo.domain.source.parser.DocsParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
class DriveSourceTest {

    @BeforeEach
    void start(){

    }

    @TestConfiguration
    static class config{

        @Bean
        public DocsSource docsSource(){
            return new DocsSource(new Member());
        }

        @Bean
        public DocsParser docsParser(){
            return new DocsParser();
        }
    }

    @Test
    public void getSource()  {
        String filename = "/auth/credentials.json";
        Source source = new Source();
        source.add("filename", filename);
    }

}
