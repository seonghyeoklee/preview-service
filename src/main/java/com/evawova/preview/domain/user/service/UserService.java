package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.SocialLoginRequest;
import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.UserUpdateRequest;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.PlanRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.security.FirebaseUserDetails;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;

    public List<UserDto> getAllUsers() {
        log.info("모든 사용자 조회 시작");
        List<User> users = userRepository.findAll();
        log.info("총 {}명의 사용자 조회 완료", users.size());
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        log.info("ID로 사용자 조회 시작: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ID로 사용자 조회 실패: 사용자를 찾을 수 없음 - ID: {}", id);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + id);
                });
        log.info("ID로 사용자 조회 성공: ID: {}", id);
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByEmail(String email) {
        log.info("이메일로 사용자 조회 시작: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("이메일로 사용자 조회 실패: 사용자를 찾을 수 없음 - 이메일: {}", email);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });
        log.info("이메일로 사용자 조회 성공: 이메일: {}", email);
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByUid(String uid) {
        log.info("UID로 사용자 조회 시작: {}", uid);
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    log.error("UID로 사용자 조회 실패: 사용자를 찾을 수 없음 - UID: {}", uid);
                    return new EntityNotFoundException("User not found with uid: " + uid);
                });
        log.info("UID로 사용자 조회 성공: UID: {}", uid);
        return UserDto.fromEntity(user);
    }

    private void updateUserRoleBasedOnPlan(User user, PlanType planType) {
        User.Role targetRole;
        switch (planType) {
            case FREE:
                targetRole = User.Role.USER_FREE;
                break;
            case STANDARD:
                targetRole = User.Role.USER_STANDARD;
                break;
            case PRO:
                targetRole = User.Role.USER_PRO;
                break;
            default:
                log.warn("Unknown PlanType: {}. Defaulting role to USER_FREE for user ID: {}", planType, user.getId());
                targetRole = User.Role.USER_FREE;
                break;
        }
        if (user.getRole() != targetRole) {
            log.info("사용자 역할 업데이트: 사용자 ID: {}, 이전 역할: {}, 새 역할: {}, 플랜 타입 기준: {}",
                    user.getId(), user.getRole(), targetRole, planType);
            user.setRole(targetRole);
        } else {
            log.debug("사용자 역할 변경 없음: 사용자 ID: {}, 현재 역할: {}", user.getId(), targetRole);
        }
    }

    @Transactional
    public UserDto socialLogin(SocialLoginRequest request) {
        log.info("소셜 로그인 처리 시작: UID: {}, 제공자: {}", request.getUid(), request.getProvider());

        User user = userRepository.findByUid(request.getUid())
                .map(existingUser -> {
                    log.info("기존 사용자 확인됨 (UID: {}). 정보 업데이트 진행.", request.getUid());
                    if (!existingUser.getProvider().equals(request.getProvider())) {
                        log.info("사용자 제공자 정보 업데이트: 사용자 ID: {}, 이전 제공자: {}, 새 제공자: {}",
                                existingUser.getId(), existingUser.getProvider(), request.getProvider());
                        existingUser.updateSocialInfo(request.getDisplayName(), request.getProvider());
                    }
                    existingUser.updateAdditionalInfo(
                            request.getDisplayName(),
                            request.getPhotoUrl(),
                            request.isEmailVerified(),
                            request.getLastLoginAt());
                    return existingUser;
                })
                .orElseGet(() -> {
                    log.info("신규 소셜 사용자 생성 시작 (UID: {}).", request.getUid());
                    Plan freePlan = planRepository.findByType(PlanType.FREE)
                            .orElseThrow(() -> {
                                log.error("치명적 오류: 소셜 로그인 중 데이터베이스에 Free 플랜이 설정되지 않았습니다.");
                                return new IllegalStateException("시스템 설정 오류: Free 플랜을 찾을 수 없습니다.");
                            });

                    User newUser = User.createSocialUser(
                            request.getUid(),
                            request.getEmail(),
                            request.getDisplayName(),
                            request.getProvider());
                    updateUserRoleBasedOnPlan(newUser, freePlan.getType());

                    User savedNewUser = userRepository.save(newUser);
                    log.info("New social user created with ID: {}", savedNewUser.getId());

                    try {
                        log.info("Creating initial FREE subscription for new social user ID: {}", savedNewUser.getId());
                        subscriptionService.createSubscription(savedNewUser.getId(), freePlan.getId(),
                                Subscription.SubscriptionCycle.MONTHLY);
                        log.info("Initial FREE subscription created successfully for new social user ID: {}",
                                savedNewUser.getId());
                    } catch (Exception e) {
                        log.error("Failed to create initial FREE subscription for new social user ID: {}. Error: {}",
                                savedNewUser.getId(), e.getMessage(), e);
                        throw new RuntimeException("초기 구독 생성 중 오류 발생", e);
                    }

                    return savedNewUser;
                });

        log.info("Social login successful for user ID: {}", user.getId());
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto changePlan(Long userId, PlanType planType) {
        log.info("Attempting to change role for user ID: {} based on requested PlanType: {}", userId, planType);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Failed to change role: User not found with ID: {}", userId);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                });

        Plan targetPlan = planRepository.findByType(planType)
                .orElseThrow(() -> {
                    log.error("Failed to change role for user ID: {}: PlanType {} not found.", userId, planType);
                    return new IllegalArgumentException("존재하지 않는 플랜 타입입니다: " + planType);
                });

        updateUserRoleBasedOnPlan(user, targetPlan.getType());

        log.warn("User role updated for user ID: {}. Actual plan change needs to be handled via SubscriptionService.",
                userId);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto changeUserPlanByUid(String uid, PlanType planType) {
        log.info("Attempting to change role for user UID: {} based on requested PlanType: {}", uid, planType);
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    log.error("Failed to change role: User not found with UID: {}", uid);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + uid);
                });

        Plan targetPlan = planRepository.findByType(planType)
                .orElseThrow(() -> {
                    log.error("Failed to change role for user UID: {}: PlanType {} not found.", uid, planType);
                    return new IllegalArgumentException("존재하지 않는 플랜 타입입니다: " + planType);
                });

        updateUserRoleBasedOnPlan(user, targetPlan.getType());

        log.warn("User role updated for user UID: {}. Actual plan change needs to be handled via SubscriptionService.",
                uid);

        User savedUser = userRepository.save(user);

        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public void withdrawUser(Long userId) {
        log.info("Withdrawing user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Withdrawal failed: User not found with ID: {}", userId);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                });

        try {
            log.info("Cancelling active subscription for withdrawing user ID: {}", userId);
            subscriptionService.cancelActiveSubscriptionByUserId(userId, "User withdrawal");
            log.info("Subscription cancellation processed for user ID: {}", userId);
        } catch (Exception e) {
            log.error(
                    "Error cancelling subscription for user ID: {} during withdrawal. Proceeding with withdrawal. Error: {}",
                    userId, e.getMessage(), e);
        }

        user.withdraw();
        log.info("User withdrawal process completed for user ID: {}", userId);
    }

    @Transactional
    public void migrateUserRoles() {
        log.info("Starting user role migration process...");
        List<User> users = userRepository.findAll();
        log.info("Found {} users to check for role migration.", users.size());
        int updatedCount = 0;
        List<User> usersToSave = new ArrayList<>();

        for (User user : users) {
            if (user.getRole() == User.Role.ADMIN) {
                log.debug("Skipping role migration for ADMIN user ID: {}", user.getId());
                continue;
            }

            Subscription activeSubscription = user.getActiveSubscription();
            PlanType currentPlanType;

            if (activeSubscription != null && activeSubscription.getPlan() != null) {
                currentPlanType = activeSubscription.getPlan().getType();
                log.debug("User ID: {} has active subscription with PlanType: {}", user.getId(), currentPlanType);
            } else {
                log.warn("User ID: {} has no active subscription. Assuming FREE plan for role migration.",
                        user.getId());
                currentPlanType = PlanType.FREE;
            }

            User.Role originalRole = user.getRole();
            updateUserRoleBasedOnPlan(user, currentPlanType);

            if (user.getRole() != originalRole) {
                updatedCount++;
                usersToSave.add(user);
                log.info("Role changed for user ID: {} from {} to {}. Added to save list.", user.getId(), originalRole,
                        user.getRole());
            }
        }

        if (!usersToSave.isEmpty()) {
            log.info("Saving {} users with updated roles...", usersToSave.size());
            userRepository.saveAll(usersToSave);
            log.info("Successfully saved updated roles.");
        } else {
            log.info("No user roles needed updating during migration.");
        }
        log.info("User role migration process completed. {} users had their roles updated.", updatedCount);
    }

    @Transactional
    public UserDto updateUser(String uid, UserUpdateRequest updateRequest) {
        log.info("Updating user information for UID: {}", uid);
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    log.error("Update failed: User not found with UID: {}", uid);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + uid);
                });

        log.debug("Current displayName: {}, Requested displayName: {}", user.getDisplayName(),
                updateRequest.getDisplayName());
        user.updateAdditionalInfo(
                updateRequest.getDisplayName(),
                user.getPhotoUrl(),
                user.isEmailVerified(),
                user.getLastLoginAt());
        log.info("User information updated successfully for UID: {}", uid);

        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        log.debug("Fetching current authenticated user");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof FirebaseUserDetails)) {
            log.error("Authentication principal is not an instance of FirebaseUserDetails: {}",
                    principal.getClass().getName());
            throw new IllegalStateException("인증 정보를 찾을 수 없습니다.");
        }

        FirebaseUserDetails userDetails = (FirebaseUserDetails) principal;
        String uid = userDetails.getUid();
        log.debug("Current user UID from security context: {}", uid);

        return userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    log.error("Authenticated user with UID: {} not found in database.", uid);
                    return new IllegalStateException("인증된 사용자 정보를 데이터베이스에서 찾을 수 없습니다.");
                });
    }
}