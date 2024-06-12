package com.example.inovation.jwt;

import com.example.inovation.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class TokenProvider {

    private final User user;

    @Value("${jwt.secretKey}")
    private String secret;
    private Long tokenValidityInMilliseconds = 1000L * 60 * 60;

    public String createToken(User user) {

    }

    public String getSubject(String token) {

    }

    public boolean validateToken(String token) {

    }
}
