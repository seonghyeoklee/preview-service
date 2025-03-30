package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUid(user.getUid());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setPlan(PlanDto.fromEntity(user.getPlan()));
        dto.setProvider(user.getProvider());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setPhotoUrl(user.getPhotoUrl());
        dto.setIsEmailVerified(user.isEmailVerified());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
} 