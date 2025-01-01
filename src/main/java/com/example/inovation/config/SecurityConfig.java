package com.example.inovation.config;

import com.example.inovation.domain.user.entity.UserRoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //role 계층화 설정
    //RoleHierarchyImpl 안됨
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = String.format("ROLE_%s > ROLE_%s",
                UserRoleType.ADMIN.toString(),
                UserRoleType.USER.toString());
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;

    }

    //시큐리티 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf 보안 해제 (개발 환경에서 설정시 복잡성)
        http.csrf(csrf -> csrf.disable());

        // 접근 경로별 인가 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll());

        // 로그인 방식 설정 Form 로그인 방식
        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

}
