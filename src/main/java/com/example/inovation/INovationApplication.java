package com.example.inovation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)//spring security 기본 로그인 없애기
public class INovationApplication {

	public static void main(String[] args) {

		SpringApplication.run(INovationApplication.class, args);
	}

}
