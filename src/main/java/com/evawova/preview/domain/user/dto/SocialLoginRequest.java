package com.evawova.preview.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.evawova.preview.domain.user.entity.User.Provider;

@Getter
@Setter
public class SocialLoginRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수입니다.")
    private String displayName;

    private Provider provider;

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String uid;

    private String photoUrl;
    private boolean isEmailVerified;
    private LocalDateTime lastLoginAt;
} 