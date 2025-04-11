package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Plan;
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
    private PlanDto activePlan;
    private User.Provider provider;
    private User.Role role;
    private boolean active;
    private String photoUrl;
    private Boolean isEmailVerified;
    private LocalDateTime lastLoginAt;

    public static UserDto fromEntity(User user) {
        // Get active subscription and its plan
        Subscription activeSubscription = user.getActiveSubscription();
        PlanDto activePlanDto = (activeSubscription != null && activeSubscription.getPlan() != null)
                ? PlanDto.fromEntity(activeSubscription.getPlan())
                : null; // Or provide a default FREE plan DTO if needed

        return UserDto.builder()
                .id(user.getId())
                .uid(user.getUid())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .activePlan(activePlanDto)
                .provider(user.getProvider())
                .role(user.getRole())
                .active(user.isActive())
                .photoUrl(user.getPhotoUrl())
                .isEmailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}