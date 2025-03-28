package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.SocialLoginRequest;
import com.evawova.preview.domain.user.dto.UserDto;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.PlanRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                freePlan
        );

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
                            freePlan
                    );
                    return userRepository.save(newUser);
                });

        // 기존 사용자면 소셜 정보 업데이트
        if (user.getProvider() != request.getProvider()) {
            user.updateSocialInfo(
                request.getDisplayName(), 
                request.getProvider()
            );
        }

        // 추가 정보 업데이트
        user.updateAdditionalInfo(
            request.getDisplayName(),
            request.getPhotoUrl(),
            request.isEmailVerified(),
            request.getLastLoginAt()
        );

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto changePlan(Long userId, PlanType planType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Plan newPlan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다: " + planType));

        user.changePlan(newPlan);
        
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
} 