package com.example.demo;

import com.example.demo.aop.aspectJ.AspectV1;
import com.example.demo.aop.postbeanprocessor.AutoProxyConfig;
import com.example.demo.domain.column.ColumnTypeConverter;
import com.example.demo.domain.validation.EntityValidator;
import com.example.demo.web.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@Import({WebConfig.class, AspectV1.class})
@Slf4j
public class DemoApplication {

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
