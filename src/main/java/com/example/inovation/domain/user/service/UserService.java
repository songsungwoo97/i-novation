package com.example.inovation.domain.user.service;

import com.example.inovation.domain.user.dto.LoginRequestDto;
import com.example.inovation.domain.user.dto.SignUpRequestDto;
import com.example.inovation.domain.user.dto.UserResponseDto;
import com.example.inovation.domain.user.entity.User;
import com.example.inovation.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    /*회원 가입*/
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto requestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 유저 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
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
        return "Bearer " + jwtTokenProvider.createToken(authentication);
    }

    private String generateToken(Authentication authentication) {
        // JWT 토큰 생성 로직 구현
        // (실제 구현은 JWT 라이브러리를 사용하여 구현)
        return "jwt-token";
    }

}
