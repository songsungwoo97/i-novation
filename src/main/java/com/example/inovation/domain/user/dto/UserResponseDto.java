package com.example.inovation.domain.user.dto;

import com.example.inovation.domain.user.entity.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private UserRoleType userRoleType;
    private LocalDateTime createdAt;
}
