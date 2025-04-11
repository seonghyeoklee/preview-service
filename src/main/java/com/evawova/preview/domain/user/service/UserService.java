package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.SocialLoginRequest;
import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.dto.UserUpdateRequest;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
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
    private final ApplicationEventPublisher eventPublisher;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByUid(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uid: " + uid));
        return UserDto.fromEntity(user);
    }

    // 사용자의 플랜 타입에 따라 적절한 역할을 설정하는 메서드
    private void updateUserRoleBasedOnPlan(User user, PlanType planType) {
        User.Role role;
        switch (planType) {
            case FREE:
                role = User.Role.USER_FREE;
                break;
            case STANDARD:
                role = User.Role.USER_STANDARD;
                break;
            case PRO:
                role = User.Role.USER_PRO;
                break;
            default:
                role = User.Role.USER_FREE;
                break;
        }
        user.setRole(role);
    }

    @Transactional
    public UserDto registerUser(String email, String password, String name) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // Free 플랜 조회
        Plan freePlan = planRepository.findByType(PlanType.FREE)
                .orElseThrow(() -> new IllegalArgumentException("Free 플랜을 찾을 수 없습니다."));

        // 사용자 생성
        User user = User.createUser(
                email,
                passwordEncoder.encode(password),
                name,
                freePlan);

        // 플랜에 따른 역할 설정
        updateUserRoleBasedOnPlan(user, freePlan.getType());

        // 이벤트 발행
        eventPublisher.publishEvent(user);

        // 저장 및 반환
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public UserDto socialLogin(SocialLoginRequest request) {
        // uid로 사용자 찾기
        User user = userRepository.findByUid(request.getUid())
                .orElseGet(() -> {
                    // FREE 플랜 조회
                    Plan freePlan = planRepository.findByType(PlanType.FREE)
                            .orElseThrow(() -> new IllegalArgumentException("Free 플랜을 찾을 수 없습니다."));

                    // 사용자가 없으면 새로 생성
                    User newUser = User.createSocialUser(
                            request.getUid(),
                            request.getEmail(),
                            request.getDisplayName(),
                            request.getProvider(),
                            freePlan);

                    // 플랜에 따른 역할 설정
                    updateUserRoleBasedOnPlan(newUser, freePlan.getType());

                    return userRepository.save(newUser);
                });

        // 기존 사용자면 소셜 정보 업데이트
        if (user.getProvider() != request.getProvider()) {
            user.updateSocialInfo(
                    request.getDisplayName(),
                    request.getProvider());
        }

        // 추가 정보 업데이트
        user.updateAdditionalInfo(
                request.getDisplayName(),
                request.getPhotoUrl(),
                request.isEmailVerified(),
                request.getLastLoginAt());

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto changePlan(Long userId, PlanType planType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Plan newPlan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다: " + planType));

        user.changePlan(newPlan);

        // 플랜에 따른 역할 업데이트
        updateUserRoleBasedOnPlan(user, planType);

        // 도메인 이벤트 발행
        eventPublisher.publishEvent(user);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 사용자 탈퇴 처리
        user.withdraw();
    }

    @Transactional
    public UserDto changeUserPlanByUid(String uid, PlanType planType) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + uid));

        Plan plan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다: " + planType));

        user.setPlan(plan);

        // 플랜에 따른 역할 업데이트
        updateUserRoleBasedOnPlan(user, planType);

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    /**
     * 모든 사용자의 역할을 현재 플랜에 맞게 업데이트합니다.
     * 이 메서드는 애플리케이션 시작 시 또는 역할 체계 변경 후 호출될 수 있습니다.
     */
    @Transactional
    public void migrateUserRoles() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // 관리자 계정은 플랜에 관계없이 ADMIN 유지
            if (user.getRole() == User.Role.ADMIN) {
                continue;
            }

            // 사용자 플랜에 따라 역할 업데이트
            updateUserRoleBasedOnPlan(user, user.getPlan().getType());
        }

        // 저장
        userRepository.saveAll(users);
        log.info("사용자 역할 마이그레이션 완료: {} 명의 사용자 업데이트됨", users.size());
    }

    /**
     * 사용자 정보를 업데이트합니다. (uid 기반)
     */
    @Transactional
    public UserDto updateUser(String uid, UserUpdateRequest updateRequest) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + uid));

        // 더티 체킹을 통한 업데이트
        user.updateAdditionalInfo(
                updateRequest.getDisplayName(),
                user.getPhotoUrl(), // 기존 값 유지
                user.isEmailVerified(), // 기존 값 유지
                user.getLastLoginAt() // 기존 값 유지
        );

        // 추후 필요한 필드가 추가될 경우 여기에 업데이트 로직 추가

        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        FirebaseUserDetails principal = (FirebaseUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByUid(principal.getUid())
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
    }
}