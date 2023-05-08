package com.example.demo;

import com.example.demo.aop.advice.LogAdvice;
import com.example.demo.aop.advice.PageAdvice;
import com.example.demo.aop.advice.TemplateAdvice;
import com.example.demo.web.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@Import({WebConfig.class, TemplateAdvice.class, LogAdvice.class, PageAdvice.class})
@Slf4j
public class DemoApplication extends WebMvcAutoConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public MessageSource messageSource(){
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasenames("messages", "errors");
		ms.setDefaultEncoding("utf-8");
		return ms;
	}
}
