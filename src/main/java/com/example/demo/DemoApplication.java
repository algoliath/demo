package com.example.demo;

import com.example.demo.web.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@Import(WebConfig.class)
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
