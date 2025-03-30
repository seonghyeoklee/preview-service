package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String uid;
    private String email;
    private String displayName;
    private PlanDto plan;
    private User.Provider provider;
    private User.Role role;
    private boolean active;
    private String photoUrl;
    private Boolean isEmailVerified;
    private LocalDateTime lastLoginAt;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
            .id(user.getId())
            .uid(user.getUid())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .plan(PlanDto.fromEntity(user.getPlan()))
            .provider(user.getProvider())
            .role(user.getRole())
            .active(user.isActive())
            .photoUrl(user.getPhotoUrl())
            .isEmailVerified(user.isEmailVerified())
            .lastLoginAt(user.getLastLoginAt())
            .build();
    }
} 