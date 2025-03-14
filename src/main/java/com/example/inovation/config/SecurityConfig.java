package com.example.inovation.config;

import com.example.inovation.domain.user.entity.UserRoleType;
import com.example.inovation.jwt.JwtAuthenticationFilter;
import com.example.inovation.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;        // JWT 토큰 생성/검증 담당
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // JWT 인증 필터

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

        // csrf 보안 비활성
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())  // 이 줄로 CORS 설정 적용
                //세션 설정: JWT를 사용하므로 세션 사용 안 함
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 접근 경로별 인가 설정
        http.authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인은 누구나 접근 가능
                .requestMatchers("/api/members/signup", "/api/members/login").permitAll()
                // 그 외의 모든 요청은 인증 필요
                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
