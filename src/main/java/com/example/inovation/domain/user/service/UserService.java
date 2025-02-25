package com.example.inovation.domain.user.service;

import com.example.inovation.domain.user.dto.LoginRequestDto;
import com.example.inovation.domain.user.dto.SignUpRequestDto;
import com.example.inovation.domain.user.dto.UserResponseDto;
import com.example.inovation.domain.user.entity.User;
import com.example.inovation.domain.user.repository.UserRepository;
import com.example.inovation.exception.BaseException;
import com.example.inovation.exception.BaseStatus;
import com.example.inovation.jwt.JwtTokenProvider;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;


    /*회원 가입*/
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto requestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 유저 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword())) // 암호화도 진행
                .name(requestDto.getName())
                .build();

        User savedUser = userRepository.save(user);
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.getCreatedDate()
        );
    }

    /*로그인*/
    @Transactional
    public String login(LoginRequestDto requestDto) {
        // UserDetails 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.getEmail());

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // 이미 검증되었으므로 credentials는 null
                userDetails.getAuthorities()
        );

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(authentication);
    }

    /*로그아웃*/
    public void addToBlackList(String token) {
        // 토큰의 남은 유효시간 계산
        long expiration = jwtTokenProvider.getExpirationTime(token);

        // Redis에 토큰을 블랙리스트로 등록
        redisTemplate.opsForValue()
                .set("blacklist:" + token, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlackListed(String token) {
        try {
            return redisTemplate.hasKey("blacklist:" + token);
        } catch (RedisConnectionException e) {
            log.error("Redis 서버 연결 실패", e);
            throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, "레디스 서버 연결 에러");
        }
    }

}
