/*
package com.example.inovation.jwt;

import com.example.inovation.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

*/
/**
 * createToken(User user): 주어진 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
 * getSubject(String token): 토큰에서 subject(여기서는 사용자의 이메일)을 추출합니다.
 * validateToken(String token): 토큰의 유효성을 검사합니다.
 *//*

@RequiredArgsConstructor
@Configuration
@Slf4j
public class TokenProvider {

    private final User user;

    @Value("${jwt.secretKey}")
    private String secret;
    private Long tokenValidityInMilliseconds = 1000L * 60 * 60;

    public String createToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getSubject(String token) {
        return null;
    }

    public boolean validateToken(String token) {
        return true;
    }
}
*/
