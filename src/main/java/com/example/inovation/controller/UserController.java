package com.example.inovation.controller;


import com.example.inovation.domain.user.dto.LoginRequestDto;
import com.example.inovation.domain.user.dto.SignUpRequestDto;
import com.example.inovation.domain.user.dto.UserResponseDto;
import com.example.inovation.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") //CORS ERROR 해결

public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createMember(@RequestBody @Valid SignUpRequestDto requestDto) {

        UserResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto requestDto) {

        String token = userService.login(requestDto);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Bearer 제거
        token = token.substring(7);

        // 토큰 블랙리스트에 추가
        userService.addToBlackList(token);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
