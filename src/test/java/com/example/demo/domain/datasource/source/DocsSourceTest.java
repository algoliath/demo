package com.example.demo.domain.datasource.source;

import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.datasource.parser.DocsParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import java.io.IOException;


@Slf4j
@SpringBootTest
@Import(DocsSourceTest.config.class)
class DocsSourceTest {

    @Autowired
    private DocsSource docsSource;

    @BeforeEach
    void start(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(DocsSourceTest.class);
        for(String beanName: ac.getBeanDefinitionNames()){
            log.info("bean:{}", beanName);
        }
        log.info("docsSourceBean:{}", ac.getBean(DocsSource.class));
    }
    @TestConfiguration
    static class config{

        @Bean
        public DocsSource docsSource(){
            return new DocsSource(docsParser());
        }

        @Bean
        public DocsParser docsParser(){
            return new DocsParser();
        }
    }

    @Test
    public void getSource() throws IOException {
        String filename = "/auth/credentials.json";
        Source source = new Source();
        source.setData("filename", filename);
        log.info("docsSource:{}", docsSource.getSource(source).getData("tableElements"));
    }

}
