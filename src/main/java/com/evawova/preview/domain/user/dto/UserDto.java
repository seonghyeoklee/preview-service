package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Subscription;
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
    private Boolean isActive;
    private String photoUrl;
    private Boolean isEmailVerified;
    private LocalDateTime lastLoginAt;

    public static UserDto fromEntity(User user) {
        Subscription activeSubscription = user.getActiveSubscription();
        PlanDto activePlanDto = (activeSubscription != null && activeSubscription.getPlan() != null)
                ? PlanDto.fromEntity(activeSubscription.getPlan())
                : null;

        return UserDto.builder()
                .id(user.getId())
                .uid(user.getUid())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .plan(activePlanDto)
                .provider(user.getProvider())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .photoUrl(user.getPhotoUrl())
                .isEmailVerified(user.getIsEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}